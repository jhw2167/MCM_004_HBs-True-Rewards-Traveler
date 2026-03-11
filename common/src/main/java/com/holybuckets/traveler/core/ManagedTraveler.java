package com.holybuckets.traveler.core;

import com.holybuckets.foundation.CommonClass;
import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.PlayerNearStructureEvent;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import com.holybuckets.foundation.event.custom.TickType;
import com.holybuckets.foundation.event.custom.WakeUpAllPlayersEvent;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import com.holybuckets.foundation.player.ManagedPlayer;
import com.holybuckets.foundation.structure.StructureAPI;
import com.holybuckets.foundation.structure.StructureInfo;
import com.holybuckets.foundation.structure.StructureManager;
import com.holybuckets.traveler.LoggerProject;
import com.holybuckets.traveler.TravelerRewardsMain;
import com.holybuckets.traveler.effect.ModEffects;
import com.holybuckets.traveler.enchantment.ModEnchantments;
import com.holybuckets.traveler.item.ModItems;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.TossItemEvent;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.holybuckets.foundation.player.ManagedPlayer.getManagedPlayer;
import static com.holybuckets.foundation.player.ManagedPlayer.registerManagedPlayerData;

/**
 * ManagedTraveler - Tracks player-specific data for HB's Traveler Rewards
 *
 * Manages:
 * - Soulbound inventory slots (survive death)
 * - Pure Heart count and health bonuses
 * - Mob Ward configurations
 * - Empty Totem wrapped items
 * - Savior Orb death location tracking
 */
public class ManagedTraveler implements IManagedPlayer {

    public static final String CLASS_ID = "020";
    private static final String MOD_DATA_KEY = "hbs_traveler_rewards";

    // Static registry of all travelers
    static final Map<String, ManagedTraveler> TRAVELERS = new ConcurrentHashMap<>();
    private final IntObjectMap<ItemStack> mobWards;
    private final IntObjectMap<ItemStack> potionPots;
    private final IntObjectMap<ItemStack> lastingItems;
    private final Set<ItemStack> weapons;

    // Player reference
    private Player player;
    private ManagedPlayer managedPlayer;
    private static Player localPlayer;
    public static ManagedTraveler localTraveler;
    private final Set<Integer> soulboundSlots; // Soulbound slot tracking (slot index -> is soulbound)
    private final IntObjectMap<ItemStack> soulboundItemsToReturn; //The items in the soulbound slots we must return to player. Must be careful if they leave the game after dying.

    private BlockPos structureEntryPos;
    private StructureInfo closestStructureInfo;
    private DeathLocation lastDeathLocation; // Death location tracking for Savior Orb
    boolean inventoryOpened;
    boolean damagedToday;
    boolean usingBuildersFlight;

    // Statistics
    private int totalDeaths;
    private int pureHeartsConsumed; // Pure Heart tracking
    private int warriorTabletsUsed;
    private int lastWarriorRitual;  //helper variable to ensure one warriorTablet used per click

    //Statics
    private static GeneralConfig GENERAL_CONFIG;
    private static ItemImplementation ITEM_IMPLEMENTATION;

    //utility

    static {
        registerManagedPlayerData( ManagedTraveler.class, () -> new ManagedTraveler(null) );
    }

    public ManagedTraveler(Player player) {
        this.player = player;
        this.soulboundItemsToReturn = new IntObjectHashMap<>();
        this.soulboundSlots = new HashSet<>();
        this.lastDeathLocation = null;
        this.pureHeartsConsumed = 0;
        this.totalDeaths = 0;
        this.warriorTabletsUsed = 0;
        this.lastWarriorRitual = -1;

        this.mobWards = new IntObjectHashMap<>();
        this.potionPots = new IntObjectHashMap<>();
        this.lastingItems = new IntObjectHashMap<>();
        this.weapons = new HashSet<>();
    }

    /**
     * Initialize event handlers
     */
    public static void init(EventRegistrar reg) {
        GENERAL_CONFIG = GeneralConfig.getInstance();
        reg.registerOnBeforeServerStarted( ManagedTraveler::onBeforeServerStarted, EventPriority.Lowest );
        reg.registerOnPlayerNearStructure(null, ManagedTraveler::onPlayerNearStructure);
        reg.registerOnServerTick(TickType.ON_SINGLE_TICK, ManagedTraveler::onServerTick);
        reg.registerOnTossItem(ManagedTraveler::onPlayerTossItem);

        reg.registerOnWakeUpAllPlayers(ManagedTraveler::onPlayersWakeUp);
    }

    private static void onPlayersWakeUp(WakeUpAllPlayersEvent wakeUpAllPlayersEvent) {
        TRAVELERS.forEach((id, traveler) -> traveler.handlePlayerWakeUp());
    }

    public static void useSoulboundTablet(ServerPlayer serverPlayer, InteractionHand hand, ItemStack stack)
    {
        ManagedTraveler traveler = ManagedTraveler.getManagedTraveler(serverPlayer);
        if(traveler == null) return;
        boolean res = traveler.addSoulboundSlot(hand, stack);
        if(res) stack.shrink(1);
        ManagedPlayer.save(serverPlayer);
    }

    public static void usePureHeart(ServerPlayer serverPlayer) {
        ManagedTraveler traveler = ManagedTraveler.getManagedTraveler(serverPlayer);
        if(traveler == null) return;
        traveler.addHealth();
        ManagedPlayer.save(serverPlayer);
    }

    public static void useWarriorRitualTablet(ServerPlayer serverPlayer) {
        ManagedTraveler traveler = ManagedTraveler.getManagedTraveler(serverPlayer);
        if(traveler == null) return;
        traveler.warriorTabletsUsed++;
    }

    public void onUseEscapeRope(ItemStack stack) {
        boolean res = ITEM_IMPLEMENTATION.onUseEscapeRope(player, structureEntryPos);
        if(res) stack.shrink(1);
    }

    //** SOULBOUND SLOT MANAGEMENT

    public static final int OFFHAND_SLOT_INDEX = 40;
    /**
     * Marks a slot as soulbound (items in this slot survive death)
     */
    private boolean addSoulboundSlot(InteractionHand hand, ItemStack stack)
    {
        int slot = getCurrentlySelectedHotbarIndex();
        if(slot == -1) return false;
        if(hand.equals(InteractionHand.MAIN_HAND)) {
            if(!player.getMainHandItem().is(ModItems.soulboundRitualTablet))
                return false;
        }
        else {
            if(!player.getOffhandItem().is(ModItems.soulboundRitualTablet))
                return false;
            slot = OFFHAND_SLOT_INDEX;
        }

        int slotToSoulbound = slot;
        boolean isSoulbound = soulboundSlots.contains(slot);
        boolean isMainHand = hand.equals(InteractionHand.MAIN_HAND);
        if(!isSoulbound) {
            //nothing, soulbound this slot
        }
        else if (isSoulbound && isMainHand) //Inventory slot becomes soulbound
        {
            for (int i = 9; i < 37; i++) {
                if(i==36) return false;   //inventory slots exhausted
                if (!soulboundSlots.contains(i)) {
                    slotToSoulbound = i;
                    break;
                }
            }
        }
        else if(isSoulbound && !isMainHand) //Offhand becomes soulbound, the armor
        {
            //internally, 36= offhand, 37 = helmet...chest, leg, boots = 40
            for (int i = OFFHAND_SLOT_INDEX; i>=35; i--) {
                if(i==35) return false;   //armor soulbound slots exhausted
                if (!soulboundSlots.contains(i)) {
                    slotToSoulbound = i;
                    break;
                }
            }
        }

        soulboundSlots.add(slotToSoulbound);
        return true;
    }

    /**
     * Removes soulbound status from a slot
     */
    public void removeSoulboundSlot(int slotIndex) {
        soulboundSlots.remove(slotIndex);
    }

    /**
     * Checks if a slot is soulbound
     */
    public boolean isSlotSoulbound(int slotIndex) {
        return soulboundSlots.contains(slotIndex);
    }

    /**
     * Gets all soulbound slot indices
     */
    public Set<Integer> getSoulboundSlots() {
        return new HashSet<>(soulboundSlots);
    }

    //** PURE HEART TRACKING

    /**
     * Records that the player consumed a Pure Heart
     */
    public void addHealth() {
        this.setHealth(++pureHeartsConsumed);
    }

    public void setHealth(int extraHearts) {
        ITEM_IMPLEMENTATION.setHealth(player, pureHeartsConsumed);
    }

    private static int distSq(BlockPos p1, BlockPos p2) {
        return  HBUtil.BlockUtil.distanceSqr(p1, p2);
    }

    private void onPlayerNearStructure(StructureInfo structureInfo)
    {
        if(closestStructureInfo == null) {
            closestStructureInfo = structureInfo;
            return;
        }

        if(structureInfo != closestStructureInfo) {
            BlockPos newPos = structureInfo.getOrigin();
            BlockPos oldPos = closestStructureInfo.getOrigin();
            BlockPos pPos = player.blockPosition();

            if(distSq(pPos, newPos) < distSq(pPos, oldPos)) {
                closestStructureInfo = structureInfo;
            }
        }
    }

    public boolean isInStructure()
    {
        return ITEM_IMPLEMENTATION.isInStructure(player);
    }

    public boolean isInDeepCaves() {
        return ITEM_IMPLEMENTATION.isInDeepCaves(player);
    }


    /**
     * Gets the total number of Pure Hearts consumed
     */
    public int getPureHeartsConsumed() {
        return pureHeartsConsumed;
    }

    //** DEATH LOCATION TRACKING

    /**
     * Records the player's death location for Savior Orb
     */
    public void setLastDeathLocation(DeathLocation location) {
        this.lastDeathLocation = location;
        
    }

    /**
     * Gets the last death location
     */
    @Nullable
    public DeathLocation getLastDeathLocation() {
        return lastDeathLocation;
    }

    /**
     * Clears the death location (e.g., after Savior Orb retrieves items)
     */
    public void clearDeathLocation() {
        this.lastDeathLocation = null;
        
    }

    public void clearSoulboundSlots() {
        soulboundSlots.clear();
    }


    public int getTotalDeaths() {
        return totalDeaths;
    }

    private static final float[] LEVEL_WEIGHTS = { 1.0f, 1.0f, 1.0f, 1.5f };

    public int getPlayerDynamicLevel() {
        int healthLevel = this.pureHeartsConsumed * 2;
        int armorLevel = this.player.getArmorValue();
        int warriorBonus = this.warriorTabletsUsed;

        float totalWeaponDamage = 0f;
        for (ItemStack weapon : this.weapons) {
            if (weapon.isEmpty()) continue;
            for (AttributeModifier modifier : weapon.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
                totalWeaponDamage += (float) modifier.getAmount();
            }
        }

        return (int)(
            (healthLevel              * LEVEL_WEIGHTS[0]) +
                (armorLevel               * LEVEL_WEIGHTS[1]) +
                (warriorBonus             * LEVEL_WEIGHTS[2]) +
                ((int) totalWeaponDamage  * LEVEL_WEIGHTS[3])
        );
    }

    //** EVENT HANDLERS


    //** PERSISTENT DATA MANAGEMENT
    
    //** STATIC UTILITY

    @Nullable
    public static ManagedTraveler getManagedTraveler(Player player) {
        if (player == null) return null;
        return TRAVELERS.get(getId(player));
    }

    //** IMANAGED_PLAYER INTERFACE IMPLEMENTATION

    @Override
    public void setPlayer(Player player)
    {
        if(player == null) return;
        if(player == this.player) return;

        if(player instanceof ServerPlayer)
        { //ServerPlayer serverPlayer server side only
            if (this.player != null)
                TRAVELERS.remove(getId(this.player));
            this.player = player;
            this.managedPlayer = ManagedPlayer.getManagedPlayer(player);
            if(GENERAL_CONFIG.isIntegrated())
                localTraveler = this;

            TRAVELERS.put(getId(player), this);
        }
        else    //clientPlayer client side only
        {
            this.localPlayer = player;
            if(!GENERAL_CONFIG.isServerSide())
                localTraveler = this;
        }
    }

    @Override
    public void setId(String s) {

    }

    public static String getId(Player p) {
        if(p==null) return null;
        return HBUtil.PlayerUtil.getId(p);
    }

    public Player getPlayer() {
        return player;
    }

    public ServerPlayer getServerPlayer() {
        return (ServerPlayer) player;
    }

    public Set<Entity> getNearbyEntities() {
        if(managedPlayer == null) return Set.of();
        return managedPlayer.getNearbyLivingEntities();
    }

    public int getCurrentlySelectedHotbarIndex() {
        if (player != null)
            return player.getInventory().selected;
        if(localPlayer != null)
            return localPlayer.getInventory().selected;
        throw new RuntimeException("Player reference is null in getCurrentlySelectedHotbarIndex");
    }


    @Override
    public boolean isServerOnly() {
        return false;
    }

    @Override
    public boolean isClientOnly() {
        return false;
    }

    @Override
    public boolean isInit(String s) {
        return player != null;
    }

    @Override
    @Nullable
    public IManagedPlayer getStaticInstance(Player player, String id) {
        return TRAVELERS.get(getId(player));
    }

    @Override
    public void handlePlayerJoin(Player player) {
        // Restore soulbound items if any are pending from death
        if (player instanceof ServerPlayer serverPlayer) {
            this.player = serverPlayer; // Update reference - dedicated servers create new player objects on rejoin
            restoreSoulboundItems();
        }
    }

    @Override
    public void handlePlayerLeave(Player player) {
        if(player==null) return;
        localTraveler = null;
        localPlayer = null;

        this.cleanupSoulboundItemsOnLeave();
    }

    /**
     * Drop soulbound items if player left before they respawned
     */
    private void cleanupSoulboundItemsOnLeave()
    {
        if(this.getServerPlayer()==null) return;
        if (soulboundItemsToReturn.isEmpty()) return;

        BlockPos dropPoint = player.blockPosition();
        if(dropPoint == null)
        {
            if(lastDeathLocation != null) {
                dropPoint = lastDeathLocation.getPosition();
            }
            else {
                if(getServerPlayer().getRespawnPosition()==null) return;
                dropPoint = getServerPlayer().getRespawnPosition();
            }
        }

        for(int i : soulboundItemsToReturn.keySet()) {
            ItemStack itemStack = soulboundItemsToReturn.get(i);
            if (!itemStack.isEmpty()) {
                Entity item = new ItemEntity(
                    getServerPlayer().level(),
                    dropPoint.getX() + 0.5,  // X position (centered)
                    dropPoint.getY() + 1.0,  // Y position (above ground)
                    dropPoint.getZ() + 0.5,  // Z position (centered)
                    itemStack
                );
                (getServerPlayer().level()).addFreshEntity(item);
            }
        }
        soulboundItemsToReturn.clear();
    }

    @Override
    public void handlePlayerRespawn(Player player) {

        if (player instanceof ServerPlayer serverPlayer)
        {
            restoreSoulboundItems();
            this.lastWarriorRitual = -1;                //reset ritual bonus on respawn
            this.setHealth(this.pureHeartsConsumed);    //set full health again on spawn
            ManagedPlayer.save(serverPlayer);
        }
    }

    @Override
    public void handlePlayerDeath(Player player)
    {
        if(!(player instanceof ServerPlayer)) return;
        totalDeaths++;

        // Record death location for Savior Orb
        lastDeathLocation = new DeathLocation(
            player.blockPosition(),
            player.level().dimension().location().toString()
        );

        // Handle soulbound slots - prevent items from dropping
        if(this.player==null) return;
        Level level = player.level();
        if( level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) ) return;
        if(!TravelerRewardsMain.CONFIG.simpleRewards.enableSoulboundSlots) return;

        if (!soulboundSlots.isEmpty()) {
            Inventory inventory = player.getInventory();
            for (int slotIndex : soulboundSlots) {
                if(inventory.getItem(slotIndex).isEmpty()) continue;
                soulboundItemsToReturn.put(slotIndex, inventory.getItem(slotIndex).copy());
                inventory.setItem(slotIndex, ItemStack.EMPTY);
            }
        }

    }

    @Override
    public void handlePlayerAttack(Player player, Entity target) {
        // Not needed for traveler rewards
    }

    @Override
    public void handlePlayerDamage(Player player, float damageAmount)
    {
        if(player != this.player) return;
        damagedToday = true;
    }

    public void handlePlayerWakeUp() {
        damagedToday = false;
    }

    /**
     * Restores soulbound items to player inventory after respawn
     */
    private void restoreSoulboundItems()
     {
         if(!(player instanceof ServerPlayer)) return;
        if (soulboundItemsToReturn.isEmpty()) return;
        //if gamerule keep inventory or config
        if(player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)) return;
        if( !TravelerRewardsMain.CONFIG.simpleRewards.enableSoulboundSlots ) return;

        Inventory inventory = player.getInventory();
        for (int i : soulboundItemsToReturn.keySet()) {
            if (!soulboundItemsToReturn.get(i).isEmpty()) {
                inventory.setItem(i, soulboundItemsToReturn.get(i));
            }
        }

        soulboundItemsToReturn.clear();
    }

    /*
        ON TICK EVENT LOOPS
     */

    /**
     * Checks all inventory slots for items of note
     */
    void takeInventoryOnTick()
    {
        //Iterate over the players entire inventory
        mobWards.clear();
        potionPots.clear();
        lastingItems.clear();
        weapons.clear();

        //user is opening  a container, we only want this true when they initially open it
        if(player.hasContainerOpen() ) {
            inventoryOpened = !inventoryOpened;
        }

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;

            //Check for mob ward
            if (stack.getItem() == ModItems.mobWard) {
                mobWards.put(i, stack);
            }

            //Check for potion pot
            if (stack.getItem() == ModItems.potionPot) {
                potionPots.put(i, stack);
            }

            boolean isWeapon = stack.is(ItemTags.SWORDS) || stack.is(ItemTags.AXES);
            if(isWeapon) {
                weapons.add(stack);
            }

            //Check for lasting enchantment
            if(!stack.isEnchanted()) continue;
            int lastingLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), stack);
            if (lastingLevel > 0) {
                lastingItems.put(i, stack);
                if(inventoryOpened && ITEM_IMPLEMENTATION.getLastingExpiration(stack) != null) {
                    Long dur = ITEM_IMPLEMENTATION.calculateLastingDuration(stack);
                    ITEM_IMPLEMENTATION.setLastingDuration(stack, dur); //will sync with client
                }
            }

        }

        //As long as inventory is open, this stays true, then false during while loop
        inventoryOpened = player.hasContainerOpen();
    }

    public void testClosestStructureOnTick()
    {
        BlockPos pPos = player.blockPosition();
        if(closestStructureInfo == null) {
            structureEntryPos = pPos.offset(0,1,0);
            return;
        }
        StructureAPI api = StructureAPI.get(this.player.level());
        if(api==null) return;
        List<StructureInfo> info = api.nearestStructures(pPos, 1);
        if(info.isEmpty()) return;
        StructureInfo s = info.get(0);
        if(!HBUtil.BlockUtil.inRange( pPos, s.getOrigin() , (int) StructureManager.NEAR_STRUCTURE_THRESHOLD)) {
            closestStructureInfo = null;
            structureEntryPos = pPos.offset(0,1,0);
        }
    }

    private void applyWarriorRitualBonusOnTick()
    {
        if(player.getAttributes() == null) return;
        if(player.getAttributes().getInstance(Attributes.ATTACK_SPEED) == null) return;

        if(lastWarriorRitual == warriorTabletsUsed) return;
        ITEM_IMPLEMENTATION.setWarriorRitualBonus(player, warriorTabletsUsed);
        lastWarriorRitual = warriorTabletsUsed;
    }

    private void updateLastingItemsOnTick() {
        ITEM_IMPLEMENTATION.checkLastingEnchantments(getServerPlayer(), lastingItems);
    }

    private void wardMobsOnTick() {
        ITEM_IMPLEMENTATION.wardMobs(getServerPlayer(), mobWards, getNearbyEntities());
    }


    private void applyBlessingsOnTick()
    {
        //if player is in creative mode, return
        if(getServerPlayer().isCreative()) return;

        Set<MobEffect> effects = getServerPlayer().getActiveEffectsMap().keySet();
        if(effects.contains(ModEffects.BLESSING_TRAVELER.get())) {
            //Waystones Free
            //Cool Breeze, Warm Winds
        }

        if(effects.contains(ModEffects.BUILDERS_FLIGHT.get()))
        {
            boolean canFly = getServerPlayer().getAbilities().mayfly;
            if(usingBuildersFlight)
            {
                if(canFly && damagedToday) {
                    //add slow falling effect
                    MobEffectInstance slowFalling = new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, true);
                    getServerPlayer().addEffect(slowFalling);
                    String message = Component.translatable("effect.hbs_traveler_rewards.builders_flight_potion.desc_lost").getString();
                    CommonClass.MESSAGER.sendBottomActionHint(player, message);

                    getServerPlayer().getAbilities().mayfly = false;
                    player.onUpdateAbilities();
                    usingBuildersFlight = false;
                }

                MobEffectInstance bFlight = getServerPlayer().getEffect(ModEffects.BUILDERS_FLIGHT.get());
                if(bFlight.getDuration() < 40) {
                    //apply slowFalling
                    MobEffectInstance slowFalling = new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, true);
                    getServerPlayer().addEffect(slowFalling);
                }
            }

            if(!canFly && !damagedToday) {
                getServerPlayer().getAbilities().mayfly = true;
                player.onUpdateAbilities();
                usingBuildersFlight = true;
            }
        }

    }


    //** NBT SERIALIZATION **/

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        // Serialize soulbound slots
        int[] slotArray = soulboundSlots.stream().mapToInt(Integer::intValue).toArray();
        tag.putIntArray("soulbound_slots", slotArray);

        tag.putInt("total_hearts", pureHeartsConsumed);
        tag.putInt("warrior_tablets", warriorTabletsUsed);

        if (structureEntryPos != null) {
            tag.putString("structure_entry_pos", HBUtil.BlockUtil.positionToString(structureEntryPos));
        }

        if (lastDeathLocation != null) {
            tag.putString("death_location", lastDeathLocation.serialize());
        }

        tag.putInt("total_deaths", totalDeaths);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag)
    {
        if (tag == null || tag.isEmpty()) return;

        // Deserialize soulbound slots
        if (tag.contains("soulbound_slots")) {
            int[] slotArray = tag.getIntArray("soulbound_slots");
            soulboundSlots.clear();
            for (int slot : slotArray) {
                soulboundSlots.add(slot);
            }
        }

        // Deserialize structure entry position
        if (tag.contains("structure_entry_pos")) {
            String posString = tag.getString("structure_entry_pos");
            Vec3i pos = HBUtil.BlockUtil.stringToBlockPos(posString);
            structureEntryPos = (pos != null) ? new BlockPos(pos) : null;
        }

        if (tag.contains("total_hearts")) {
            pureHeartsConsumed = tag.getInt("total_hearts");
            if(ITEM_IMPLEMENTATION!=null) //serverSide only
                ITEM_IMPLEMENTATION.setHealth(player, pureHeartsConsumed);
        }
        if (tag.contains("warrior_tablets")) {
            warriorTabletsUsed = tag.getInt("warrior_tablets");
            lastWarriorRitual = -1;    //reset ritual bonus so it will be applied on next tick
        }
        if (tag.contains("death_location")) {
            lastDeathLocation = DeathLocation.deserialize(tag.getString("death_location"));
        }
        if (tag.contains("total_deaths")) {
            totalDeaths = tag.getInt("total_deaths");
        }
    }


    //** EVENTS
    public static void onBeforeServerStarted(ServerStartingEvent event) {
        GENERAL_CONFIG = GeneralConfig.getInstance();
        ITEM_IMPLEMENTATION = ItemImplementation.getInstance();
    }

    public static void onPlayerNearStructure(PlayerNearStructureEvent event) {
        Player player = event.getPlayer();
        ManagedTraveler traveler = ManagedTraveler.getManagedTraveler(player);
        if(traveler == null) return;
        traveler.onPlayerNearStructure(event.getStructureInfo());
    }

    // Add the tick handler method
    /**
     * Called every 20 server ticks to check for Lasting enchantment expiration
     */
    private static int count=0;
    private static void onServerTick(ServerTickEvent event)
    {
        if(count++<10) return;
        count=0;

        for (ManagedTraveler traveler : TRAVELERS.values()) {
            if(traveler.player == null) continue;
            if (traveler.player instanceof ServerPlayer serverPlayer) {
                traveler.takeInventoryOnTick();
                traveler.applyWarriorRitualBonusOnTick();
                traveler.testClosestStructureOnTick();
                traveler.updateLastingItemsOnTick();
                traveler.wardMobsOnTick();
                traveler.applyBlessingsOnTick();
            }
        }
    }


    /**
         * Player Drop Item
         */
    private static void onPlayerTossItem(TossItemEvent event)
    {
        Player player = event.getPlayer();
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        ItemStack stack = event.getItemStack();
        if(stack.getItem() == ModItems.potionPot) {
            ITEM_IMPLEMENTATION.handlePotionPotToss(serverPlayer, stack);
        }

    }

    public Collection<ItemStack> getLastingItems() {
        return lastingItems.values();
    }

    public static final long DAY_LENGTH_TICKS = 24000;
    public void appendLastingTooltip(ItemStack stack, List<Component> tooltip)
    {
        //clientSide default
       Long ticksLeft =  ItemImplementation.getLastingDuration(stack);
       if(GeneralConfig.getInstance().isIntegrated()) {
            ticksLeft = ITEM_IMPLEMENTATION.calculateLastingDuration(stack);
       }

       if(ticksLeft == null) return;

       String timeLeft;
       if(ticksLeft > DAY_LENGTH_TICKS) {
           timeLeft = String.format("%.0f days", (float)ticksLeft / DAY_LENGTH_TICKS);
       }
       else if(ticksLeft > 5*1200) {
           timeLeft = String.format("%.0f minutes", (float)ticksLeft / 1200);
       }
       else {
           timeLeft = String.format("%d seconds", ticksLeft / 20);
       }

        tooltip.add(Component.translatable(
                "tooltip.hbs_traveler_rewards.lasting.time_remaining", timeLeft)
            .withStyle(style -> style
                .withColor(ChatFormatting.RED)
                .withItalic(true)
            ));
    }


    //** INNER CLASSES

    /**
     * Represents a death location for Savior Orb tracking
     */
    public static class DeathLocation {
        private final int x, y, z;
        private final String dimensionId;

        public DeathLocation(net.minecraft.core.BlockPos pos, String dimensionId) {
            this.x = pos.getX();
            this.y = pos.getY();
            this.z = pos.getZ();
            this.dimensionId = dimensionId;
        }

        private DeathLocation(int x, int y, int z, String dimensionId) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimensionId = dimensionId;
        }

        public net.minecraft.core.BlockPos getPosition() {
            return new net.minecraft.core.BlockPos(x, y, z);
        }

        public String getDimensionId() {
            return dimensionId;
        }

        public String serialize() {
            return String.format("%d,%d,%d|%s", x, y, z, dimensionId);
        }

        @Nullable
        public static DeathLocation deserialize(String data) {
            try {
                String[] parts = data.split("\\|");
                if (parts.length != 2) return null;

                String[] coords = parts[0].split(",");
                if (coords.length != 3) return null;

                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int z = Integer.parseInt(coords[2]);
                String dimensionId = parts[1];

                return new DeathLocation(x, y, z, dimensionId);
            } catch (Exception e) {
                LoggerProject.logError(null, CLASS_ID + "001", "Failed to deserialize DeathLocation: " + data);
                return null;
            }
        }
    }
}
