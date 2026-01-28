package com.holybuckets.traveler.core;

import com.holybuckets.foundation.GeneralConfig;
import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.LoggerBase;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.PlayerNearStructureEvent;
import com.holybuckets.foundation.event.custom.ServerTickEvent;
import com.holybuckets.foundation.event.custom.TickType;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import com.holybuckets.foundation.player.ManagedPlayer;
import com.holybuckets.foundation.structure.StructureAPI;
import com.holybuckets.foundation.structure.StructureInfo;
import com.holybuckets.foundation.structure.StructureManager;
import com.holybuckets.traveler.LoggerProject;
import com.holybuckets.traveler.TravelerRewardsMain;
import com.holybuckets.traveler.config.ModConfig;
import com.holybuckets.traveler.enchantment.ModEnchantments;
import com.holybuckets.traveler.item.ModItems;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.TossItemEvent;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.holybuckets.foundation.CommonClass.MESSAGER;
import static com.holybuckets.foundation.player.ManagedPlayer.registerManagedPlayerData;
import static  com.holybuckets.foundation.HBUtil.BlockUtil;

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

    //Item Based
    private final IntObjectMap<ItemStack> mobWards;
    private final IntObjectMap<ItemStack> potionPots;
    private final IntObjectMap<ItemStack> lastingItems;



    // Statistics
    private int totalDeaths;
    private int pureHeartsConsumed; // Pure Heart tracking

    //Statics
    private static GeneralConfig GENERAL_CONFIG;

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

        this.mobWards = new IntObjectHashMap<>();
        this.potionPots = new IntObjectHashMap<>();
        this.lastingItems = new IntObjectHashMap<>();
    }

    /**
     * Initialize event handlers
     */
    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted( ManagedTraveler::onBeforeServerStarted, EventPriority.Lowest );
        reg.registerOnPlayerNearStructure(null, ManagedTraveler::onPlayerNearStructure);
        reg.registerOnServerTick(TickType.ON_20_TICKS, ManagedTraveler::onServer20ticks );
        reg.registerOnTossItem(ManagedTraveler::onPlayerTossItem);
    }

    private static final UUID PURE_HEART_MODIFIER_UUID = UUID.fromString("a3d89f7e-5c8d-4f3a-9b2e-1d4c6e8f0a1b");
    private static final String PURE_HEART_MODIFIER_NAME = "Pure Heart";
    private static final double HEALTH_PER_HEART = 2.0;
    public static void usePureHeart(ServerPlayer player)
    {
        ManagedTraveler mt = ManagedTraveler.getManagedTraveler(player);
        mt.addHealth(HEALTH_PER_HEART);
    }

    public static void useSoulboundTablet(ServerPlayer player, InteractionHand hand, ItemStack stack)
    {
        ManagedTraveler mt = ManagedTraveler.getManagedTraveler(player);
        mt.addSoulboundSlot(hand, stack);
    }

    //** SOULBOUND SLOT MANAGEMENT

    /**
     * Marks a slot as soulbound (items in this slot survive death)
     */
    private void addSoulboundSlot(InteractionHand hand, ItemStack stack)
    {
        //1. Parse inventory for slot that matches this stack
        Inventory inventory = player.getInventory();
        int slot = inventory.findSlotMatchingItem(stack);
        //2. Check if the slot is already soulbound
        if(slot == -1) return;
        int slotToSoulbound = slot;
        if (soulboundSlots.contains(slot)) {
            //set to first non soulbound slot in players internal inventory (not armor or offhand)
            //skip hotbar and armor slots
            for (int i = 9; i < 36; i++) {
                if (!soulboundSlots.contains(i)) {
                    slotToSoulbound = i;
                    break;
                }
            }
        }

        soulboundSlots.add(slotToSoulbound);
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
    public void addHealth(double health)
    {
        AttributeInstance healthAttribute = this.player.getAttribute(Attributes.MAX_HEALTH);
        if (healthAttribute == null) {
            LoggerProject.logError("020002", "Failed to retrieve MAX_HEALTH attribute for unkown reason, player: " );
            return;
        }


        // Check if player already has the modifier (for stacking multiple pure hearts)
        AttributeModifier existingModifier = healthAttribute.getModifier(PURE_HEART_MODIFIER_UUID);

        double currentBonus = existingModifier != null ? existingModifier.getAmount() : 0.0;
        double newBonus = currentBonus + health;

        // Remove existing modifier if present
        if (existingModifier != null) {
            healthAttribute.removeModifier(PURE_HEART_MODIFIER_UUID);
        }

        // Add new modifier with increased health
        AttributeModifier newModifier = new AttributeModifier(
            PURE_HEART_MODIFIER_UUID, PURE_HEART_MODIFIER_NAME, newBonus,
            AttributeModifier.Operation.ADDITION
        );

        healthAttribute.addPermanentModifier(newModifier);

        // Heal player to new max health
        player.setHealth(player.getMaxHealth());

        // Send feedback message
        //int totalHearts = (int) (newBonus / HEALTH_PER_HEART);
        //player.sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.pure_heart.success", totalHearts));

        pureHeartsConsumed++;
    }

    private void onPlayerNearStructure(StructureInfo structureInfo)
    {
        if(structureInfo != closestStructureInfo) {
            structureEntryPos = player.blockPosition().offset(0,1,0);
            closestStructureInfo = structureInfo;
        }
    }

    public boolean isInStructure()
    {
        StructureAPI api = TravelerRewardsMain.STRUCTURE_APIS.get(player.level());
        BlockPos structurePos = api.nearestStructures(player.blockPosition(),1).get(0).getOrigin();
        if(structurePos == null) return false;
        if(BlockUtil.inRange(player.blockPosition(), structurePos, 64))
            return true;
        return false;
    }

    public static final int ESCAPE_ROPE_MAX_Y_CAVE_ESCAPE = 16;
    public boolean isInDeepCaves() {
        return (player.blockPosition().getY() < ESCAPE_ROPE_MAX_Y_CAVE_ESCAPE);
    }

    /**
     * Finds the surface directly above the player's current position
     * @return BlockPos at surface level, or null if not found
     */
    @Nullable
    private BlockPos findSurfaceAbove()
    {
        if (!(player instanceof ServerPlayer serverPlayer)) return null;

        Level level = serverPlayer.level();
        BlockPos playerPos = player.blockPosition();
        int x = playerPos.getX();
        int z = playerPos.getZ();

        // Get the top solid block at this X,Z coordinate
        // This uses Minecraft's built-in heightmap which tracks the surface
        int surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
        BlockPos surfacePos = new BlockPos(x, surfaceY, z);
        if (level.getBlockState(surfacePos).isAir() &&
            level.getBlockState(surfacePos.above()).isAir()) {
            return surfacePos;
        }

        return null;
    }

    public void onUseEscapeRope()
    {

        if(isInStructure() && structureEntryPos != null)
        {
            player.teleportTo(
                structureEntryPos.getX() + 0.5, structureEntryPos.getY(), structureEntryPos.getZ() + 0.5
            );
        } else if( isInDeepCaves() ) {
            BlockPos surface = findSurfaceAbove();
            if(surface != null)
                player.teleportTo( surface.getX() + 0.5, surface.getY(), surface.getZ() + 0.5);
        }

        structureEntryPos = null;
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

    //** STATISTICS

    public int getTotalDeaths() {
        return totalDeaths;
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
        if(player == this.player || player == this.localPlayer) return;

        if(player instanceof ServerPlayer)
        { //ServerPlayer serverPlayer server side only
            if (this.player != null)
                TRAVELERS.remove(getId(this.player));
            this.player = player;
            this.managedPlayer = ManagedPlayer.getManagedPlayer(player);
        }
        else    //clientPlayer client side only
        {

        }
        if(localPlayer!=null)
            TRAVELERS.remove(getId(localPlayer));
        if(ManagedPlayer.CLIENT_PLAYER!=null)
            this.localPlayer = ManagedPlayer.CLIENT_PLAYER.getPlayer();
        this.localTraveler = this;
        TRAVELERS.put(getId(player), this);
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
        return managedPlayer.getNearbyEntities();
    }

    public int getCurrentlySelectedHotbarIndex() {
        if (player == null) return -1;
        return player.getInventory().selected;
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
            restoreSoulboundItems();
        }
    }

    @Override
    public void handlePlayerLeave(Player player) {
        if(player==null) return;
        localTraveler = null;
        localPlayer = null;

        this.cleanupSoulboundItemsOnLeave();
        TRAVELERS.remove(getId(player));
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
        // Restore soulbound items after respawn
        if (player instanceof ServerPlayer serverPlayer) {
            restoreSoulboundItems();
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
        if (!soulboundSlots.isEmpty()) {
            Inventory inventory = player.getInventory();
            for (int slotIndex : soulboundSlots) {
                soulboundItemsToReturn.put(slotIndex, inventory.getItem(slotIndex));
                inventory.setItem(slotIndex, ItemStack.EMPTY);
            }
        }

    }

    @Override
    public void handlePlayerAttack(Player player, Entity target) {
        // Not needed for traveler rewards
    }

    /**
     * Restores soulbound items to player inventory after respawn
     */
    private void restoreSoulboundItems()
     {
         if(!(player instanceof ServerPlayer)) return;
        if (soulboundItemsToReturn.isEmpty()) return;

        Inventory inventory = player.getInventory();
        for (int i : soulboundItemsToReturn.keySet()) {
            if (!soulboundItemsToReturn.get(i).isEmpty()) {
                inventory.setItem(i, soulboundItemsToReturn.get(i));
            }
        }
        soulboundItemsToReturn.clear();
    }


    /**
     * Checks all items in player inventory for Lasting enchantment
     * Tracks expiration time and removes expired items
     */
    private void checkLastingEnchantments()
    {
        Inventory inventory = player.getInventory();
        long currentTick = GENERAL_CONFIG.getTotalTickCount();

        List<Integer> slotsToRemove = new ArrayList<>();
        for (Integer i : lastingItems.keySet())
        {
            ItemStack stack = inventory.getItem(i);
            if (stack.isEmpty()) continue;
            if(!stack.isEnchanted()) continue;

            int lastingLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), stack);
            if (lastingLevel > 0)
            {
                Long expirationTick = getLastingExpiration(stack);
                if (expirationTick == null)
                {
                    expirationTick = calculateLastingExpiration(stack, currentTick);
                    setLastingExpiration(stack, expirationTick);
                    LoggerProject.logDebug("020003",
                        String.format("Player %s has new Lasting item: %s, expires at tick %d",
                            player.getName().getString(), stack.getDisplayName().getString(), expirationTick));
                }

                // Check if item has expired
                if (currentTick >= expirationTick) {
                    slotsToRemove.add(i);

                    LoggerProject.logInfo("020004",
                        String.format("Lasting item expired for player %s: %s (tick %d >= %d)",
                            player.getName().getString(), stack.getDisplayName().getString(),
                            currentTick, expirationTick));
                }
            } else {
                removeLastingExpiration(stack);
            }
        }

        // Remove expired items
        for (int slot : slotsToRemove)
        {
            ItemStack expiredStack = inventory.getItem(slot);
            removeLastingExpiration(expiredStack);
            inventory.setItem(slot, ItemStack.EMPTY);

            // Optional: Notify player
            MESSAGER.sendBottomActionHint(
                Component.translatable("enchantment.hbs_traveler_rewards.lasting.expired",
                    expiredStack.getDisplayName()).getString()
            );
        }
    }

    private long calculateLastingExpiration(ItemStack stack, long currentTick)
    {
        if (stack.hasTag() && stack.getTag().contains("LastingDuration")) {
            long customDuration = stack.getTag().getLong("LastingDuration");
            return currentTick + customDuration;
        }
        int lastingLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), stack);
        if(lastingLevel == 0) {
            stack.enchant(ModEnchantments.LASTING.get(), 1);
            lastingLevel = 1;
        }
        int duration = ModConfig.LASTING_TICKS[ Math.min(9, lastingLevel - 1) ];
        return currentTick + duration;
    }


    @Nullable
    private Long getLastingExpiration(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("LastingExpiration")) {
            return stack.getTag().getLong("LastingExpiration");
        }
        return null;
    }

    private void setLastingExpiration(ItemStack stack, long expirationTick) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        stack.getTag().putLong("LastingExpiration", expirationTick);
    }


    private void removeLastingExpiration(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("LastingExpiration")) {
            stack.getTag().remove("LastingExpiration");
        }
    }


    /**
     * Checks all inventory slots for items of note
     */
    private void takeInventory()
    {
        //Iterate over the players entire inventory
        mobWards.clear();
        potionPots.clear();
        lastingItems.clear();

        ServerPlayer sp = getServerPlayer();
        Inventory inventory = sp.getInventory();
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

            //Check for lasting enchantment
            if(!stack.isEnchanted()) continue;
            int lastingLevel = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), stack);
            if (lastingLevel > 0) {
                lastingItems.put(i, stack);
            }
        }
    }

    //** Mob Ward **//
    private void wardMobs()
    {
        for (ItemStack mobWardStack : mobWards.values()) {
            if(mobWardStack.getTag().contains("filterItem")) {
                ItemStack filterItem = ItemStack.of(mobWardStack.getTag().getCompound("filterItem"));
                getNearbyEntities().forEach(e -> wardEntity(e, player, filterItem));
            }
        }
    }

    private static void wardEntity(Entity entity, Player player, ItemStack filterItem) {
        if(!(entity instanceof Mob mob)) return;
        //Find
        wardMob(mob, player);
    }

    private static void wardMob(Mob mob, Player player)
    {
        if (mob.getTarget() == player) mob.setTarget(null);
        mob.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ATTACK_TARGET);
        mob.getBrain().eraseMemory(net.minecraft.world.entity.ai.memory.MemoryModuleType.ANGRY_AT);
        boolean tooClose = HBUtil.BlockUtil.inRange(mob.blockPosition(), player.blockPosition(), 16);
        if(tooClose) addFleeGoal(mob, player);
    }

    private static void addFleeGoal(Mob mob, Player player) {
        Vec3 fleeDirection = mob.position().subtract(player.position()).normalize();
        Vec3 fleeTarget = mob.position().add(fleeDirection.scale(5.0)); // Flee 5 blocks away
        mob.getNavigation().moveTo(fleeTarget.x, fleeTarget.y, fleeTarget.z, 1.2); // 1.2 = movement speed multiplier
    }



    //** POTION POT **//

    private static List<ItemStack> brewPotionPot(ServerPlayer player, ItemStack potionPotStack)
    {
        if (!potionPotStack.hasTag()) return List.of();
        CompoundTag tag = potionPotStack.getTag();

        int awkwardPotionCount = tag.getInt("AwkwardPotionCount");
        if (awkwardPotionCount <= 0) return List.of();

        List<ItemStack> potions = new ArrayList<>();
        for(int i=0; i<awkwardPotionCount; i++) {
            ItemStack awkwardPotion = new ItemStack(Items.POTION);
            PotionUtils.setPotion(awkwardPotion, Potions.AWKWARD);
            potions.add(awkwardPotion);
        }

        if (!tag.contains("Ingredient")) return potions;

        ItemStack ingredient = ItemStack.of(tag.getCompound("Ingredient"));
        if (ingredient.isEmpty()) return potions;
        if (!PotionBrewing.hasMix(ingredient, potions.get(0))) return potions;

        List<ItemStack> brewedPotions = new ArrayList<>();
        for(ItemStack basePotion : potions) {
            brewedPotions.add( PotionBrewing.mix(ingredient, basePotion) );
        }

        return brewedPotions;
    }



    //** NBT SERIALIZATION **/

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        // Serialize soulbound slots
        int[] slotArray = soulboundSlots.stream().mapToInt(Integer::intValue).toArray();
        tag.putIntArray("soulbound_slots", slotArray);

        // Serialize Pure Heart count
        tag.putInt("total_hearts", pureHeartsConsumed);

        // Serialize structure entry position
        if (structureEntryPos != null) {
            tag.putString("structure_entry_pos", HBUtil.BlockUtil.positionToString(structureEntryPos));
        }

        // Serialize death location
        if (lastDeathLocation != null) {
            tag.putString("death_location", lastDeathLocation.serialize());
        }

        // Serialize statistics
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
    private static void onServer20ticks(ServerTickEvent event) {
        for (ManagedTraveler traveler : TRAVELERS.values()) {
            if (traveler.player instanceof ServerPlayer serverPlayer) {
                traveler.checkLastingEnchantments();
                traveler.wardMobs();
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
        if(stack.getItem() == ModItems.potionPot)
        {
            List<ItemStack> dropItems = brewPotionPot(serverPlayer, stack);
            stack.shrink(1); // Consume one Potion Pot item
            serverPlayer.level().playSound(null, serverPlayer.blockPosition(),
                SoundEvents.GLASS_BREAK, SoundSource.PLAYERS,
                1.0f, 1.0f
            );

            if(!dropItems.isEmpty())
            {
                Vec3 inFront = serverPlayer.getEyePosition().add(serverPlayer.getLookAngle().scale(1.5));
                for(ItemStack dropStack : dropItems)
                {
                    ItemEntity itemEntity = new ItemEntity( serverPlayer.level(),
                        inFront.x, inFront.y, inFront.z, dropStack
                    );
                    serverPlayer.level().addFreshEntity(itemEntity);
                }
            }

        }

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
                LoggerBase.logError(null, CLASS_ID + "001", "Failed to deserialize DeathLocation: " + data);
                return null;
            }
        }
    }
}