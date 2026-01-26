package com.holybuckets.traveler.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.LoggerBase;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import com.holybuckets.foundation.player.ManagedPlayer;
import com.holybuckets.traveler.LoggerProject;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    // Player reference
    private Player player;
    private static Player localPlayer;
    public static ManagedTraveler localTraveler;
    private final Set<Integer> soulboundSlots; // Soulbound slot tracking (slot index -> is soulbound)
    private final IntObjectMap<ItemStack> soulboundItemsToReturn; //The items in the soulbound slots we must return to player. Must be careful if they leave the game after dying.
    private DeathLocation lastDeathLocation; // Death location tracking for Savior Orb
    private int pureHeartsConsumed; // Pure Heart tracking

    // Statistics
    private int totalDeaths;

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
    }

    /**
     * Initialize event handlers
     */
    public static void init(EventRegistrar reg) {

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
                soulboundItemsToReturn.put(slotIndex, inventory.getItem(slotIndex).copy());
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

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag tag = new CompoundTag();

        // Serialize soulbound slots
        int[] slotArray = soulboundSlots.stream().mapToInt(Integer::intValue).toArray();
        tag.putIntArray("soulbound_slots", slotArray);

        // Serialize Pure Heart count
        tag.putInt("total_hearts", pureHeartsConsumed);

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

    @Override
    public void setId(String s) {
        
    }


    //** EVENTS


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