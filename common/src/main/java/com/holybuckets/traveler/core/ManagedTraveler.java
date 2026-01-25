package com.holybuckets.traveler.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.LoggerBase;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.modelInterface.IManagedPlayer;
import net.blay09.mods.balm.api.event.LivingDeathEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
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
    static final Map<Player, ManagedTraveler> TRAVELERS = new ConcurrentHashMap<>();

    // Player reference
    private Player player;

    // Soulbound slot tracking (slot index -> is soulbound)
    private final Set<Integer> soulboundSlots;

    // Death location tracking for Savior Orb
    private DeathLocation lastDeathLocation;

    // Pure Heart tracking
    private int pureHeartsConsumed;

    // Statistics
    private int totalDeaths;
    private int itemsSavedBySoulbound;

    static {
        registerManagedPlayerData(
            ManagedTraveler.class,
            () -> new ManagedTraveler(null)
        );
    }

    public ManagedTraveler(Player player) {
        this.player = player;
        this.soulboundSlots = new HashSet<>();
        this.lastDeathLocation = null;
        this.pureHeartsConsumed = 0;
        this.totalDeaths = 0;
        this.itemsSavedBySoulbound = 0;
    }

    /**
     * Initialize event handlers
     */
    public static void init(EventRegistrar reg) {
        reg.registerOnPlayerDeath(ManagedTraveler::onPlayerDeath);
    }

    //** SOULBOUND SLOT MANAGEMENT

    /**
     * Marks a slot as soulbound (items in this slot survive death)
     */
    public void addSoulboundSlot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 41) { // 36 inventory + 4 armor + 1 offhand = 41 slots
            soulboundSlots.add(slotIndex);
            
        }
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
    public void addPureHeart() {
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

    public int getItemsSavedBySoulbound() {
        return itemsSavedBySoulbound;
    }

    //** EVENT HANDLERS

    /**
     * Called when any player dies - handles soulbound slot preservation
     */
    private static void onPlayerDeath(LivingDeathEvent event) {
        if(event.getEntity() instanceof Player player) {
            if (!(player instanceof ServerPlayer serverPlayer)) return;

            ManagedTraveler traveler = TRAVELERS.get(player);
            if (traveler == null) return;

            traveler.handlePlayerDeath(serverPlayer, event);
        }

    }

    /**
     * Handles death logic for this traveler
     */
    private void handlePlayerDeath(ServerPlayer player, LivingDeathEvent event) {
        totalDeaths++;

        // Record death location for Savior Orb
        lastDeathLocation = new DeathLocation(
            player.blockPosition(),
            player.level().dimension().location().toString()
        );

        // Handle soulbound slots - prevent items from dropping
        if (!soulboundSlots.isEmpty()) {
            Inventory inventory = player.getInventory();
            List<ItemStack> soulboundItems = new ArrayList<>();

            for (int slotIndex : soulboundSlots) {
                ItemStack stack = inventory.getItem(slotIndex);
                if (!stack.isEmpty()) {
                    soulboundItems.add(stack.copy());
                    itemsSavedBySoulbound += stack.getCount();
                }
            }

            // Store soulbound items to be restored on respawn
            if (!soulboundItems.isEmpty())
            {
            
            }
        }

        
    }

    //** PERSISTENT DATA MANAGEMENT
    
    //** STATIC UTILITY

    @Nullable
    public static ManagedTraveler getManagedTraveler(Player player) {
        if (player == null) return null;
        return TRAVELERS.get(player);
    }

    @Nullable
    public static ManagedTraveler getOrCreate(Player player) {
        if (player == null) return null;
        return TRAVELERS.computeIfAbsent(player, ManagedTraveler::new);
    }

    //** IMANAGED_PLAYER INTERFACE IMPLEMENTATION

    @Override
    public void setPlayer(Player player) {
        if (this.player == null) {
            this.player = player;
            TRAVELERS.put(player, this);
            
            return;
        }

        ManagedTraveler traveler = TRAVELERS.remove(this.player);
        this.player = player;
        TRAVELERS.put(player, traveler);
    }


    @Override
    public boolean isServerOnly() {
        return true;
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
        return TRAVELERS.get(player);
    }

    @Override
    public void handlePlayerJoin(Player player) {
        

        // Restore soulbound items if any are pending from death
        if (player instanceof ServerPlayer serverPlayer) {
            restoreSoulboundItems(serverPlayer);
        }
    }

    @Override
    public void handlePlayerLeave(Player player) {
        
    }

    @Override
    public void handlePlayerRespawn(Player player) {
        // Restore soulbound items after respawn
        if (player instanceof ServerPlayer serverPlayer) {
            restoreSoulboundItems(serverPlayer);
        }
    }

    @Override
    public void handlePlayerDeath(Player player) {
        // Handled by the static event handler
    }

    @Override
    public void handlePlayerAttack(Player player, Entity target) {
        // Not needed for traveler rewards
    }

    /**
     * Restores soulbound items to player inventory after respawn
     */
    private void restoreSoulboundItems(ServerPlayer player) {
       
    }

    @Override
    public CompoundTag serializeNBT() {
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
        tag.putInt("items_saved", itemsSavedBySoulbound);

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag == null || tag.isEmpty()) return;

        // Deserialize soulbound slots
        if (tag.contains("soulbound_slots")) {
            int[] slotArray = tag.getIntArray("soulbound_slots");
            soulboundSlots.clear();
            for (int slot : slotArray) {
                soulboundSlots.add(slot);
            }
        }

        // Deserialize Pure Heart count
        if (tag.contains("total_hearts")) {
            pureHeartsConsumed = tag.getInt("total_hearts");
        }

        // Deserialize death location
        if (tag.contains("death_location")) {
            lastDeathLocation = DeathLocation.deserialize(tag.getString("death_location"));
        }

        // Deserialize statistics
        if (tag.contains("total_deaths")) {
            totalDeaths = tag.getInt("total_deaths");
        }
        if (tag.contains("items_saved")) {
            itemsSavedBySoulbound = tag.getInt("items_saved");
        }
    }

    @Override
    public void setId(String s) {
        
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