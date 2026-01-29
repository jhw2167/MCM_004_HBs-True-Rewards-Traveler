package com.holybuckets.traveler.config;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.LoggerBase;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.traveler.LoggerProject;
import com.holybuckets.traveler.TravelerRewardsMain;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.blay09.mods.balm.api.event.server.ServerStoppedEvent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import java.lang.reflect.Field;
import java.util.*;

public class ModConfig {

    public static Set<Block> validFabricatedBlocks = new HashSet<>();
    public static Set<Item> validWhetstoneItems = new HashSet<>();
    public static Set<Item> validBracingItems = new HashSet<>();
    public static Set<Item> validDiamondShardItems = new HashSet<>();
    public static Set<Item> validIronBloomItems = new HashSet<>();
    public static Set<Item> validGoldRepairItems = new HashSet<>();
    public static Set<Item> validNetheriteRepairItems = new HashSet<>();

    private static final Map<EntityType<?>, Set<Item>> mobDrops = new HashMap<>();
    private static final Map<ResourceLocation, Set<Item>> mobLootTables = new HashMap<>();

    public static final int[] LASTING_TICKS = new int[] {
        300,
        6000,   // Level 1 - 5 minutes
        12000,  // Level 2 - 10 minutes
        24000,  // Level 3 - keep doubling
        48000,
        96000,
        192000,
        384000, //16 days
        768000,
        1536000, //64 days
    };

    public static void init(EventRegistrar reg ) {
        reg.registerOnBeforeServerStarted( ModConfig::onServerStarting, EventPriority.High );
        reg.registerOnServerStopped( ModConfig::onServerStopped, EventPriority.Low );
    }

    private static void onServerStarting(ServerStartingEvent event)
    {
        TravelerRewardsConfig CONFIG = TravelerRewardsMain.CONFIG;

        validBracingItems.clear();
        validDiamondShardItems.clear();
        validFabricatedBlocks.clear();
        validGoldRepairItems.clear();
        validIronBloomItems.clear();
        validNetheriteRepairItems.clear();
        validWhetstoneItems.clear();

        // Convert fabrication blocks
        for( String blockId : CONFIG.simpleRewards.fabricationRitualBlocksWhitelist ) {
            Block block = HBUtil.BlockUtil.blockNameToBlock(blockId);
            if( block != null ) {
                validFabricatedBlocks.add(block);
            }
        }
        
        // Convert anvil reward items
        for( String itemId : CONFIG.anvilRewards.whetstoneWeapons ) {
            Item item = HBUtil.ItemUtil.itemNameToItem(itemId);
            if( item != null ) {
                validWhetstoneItems.add(item);
            }
        }
        
        for( String itemId : CONFIG.anvilRewards.bracingEquipment ) {
            Item item = HBUtil.ItemUtil.itemNameToItem(itemId);
            if( item != null ) {
                validBracingItems.add(item);
            }
        }
        
        for( String itemId : CONFIG.anvilRewards.diamondRepairEquip ) {
            Item item = HBUtil.ItemUtil.itemNameToItem(itemId);
            if( item != null ) {
                validDiamondShardItems.add(item);
            }
        }
        
        for( String itemId : CONFIG.anvilRewards.ironRepairEquip ) {
            Item item = HBUtil.ItemUtil.itemNameToItem(itemId);
            if( item != null ) {
                validIronBloomItems.add(item);
            }
        }
        
        for( String itemId : CONFIG.anvilRewards.goldRepairEquip ) {
            Item item = HBUtil.ItemUtil.itemNameToItem(itemId);
            if( item != null ) {
                validGoldRepairItems.add(item);
            }
        }
        
        for( String itemId : CONFIG.anvilRewards.netheriteRepairEquip ) {
            Item item = HBUtil.ItemUtil.itemNameToItem(itemId);
            if( item != null ) {
                validNetheriteRepairItems.add(item);
            }
        }

        //Init Mob Drops by scanning Entity Type and Loot Tables Registries
        Registry<EntityType<?>> entities = BuiltInRegistries.ENTITY_TYPE;
        for( ResourceLocation entityLoc : mobLootTables.keySet() )
        {
            EntityType<?> entityType = entities.get(entityLoc);
            if( entityType == null ) continue;
            Set<Item> drops = mobDrops.getOrDefault( entityType, new HashSet<>() );
            drops.addAll( mobLootTables.get(entityLoc) );
            mobDrops.put( entityType, drops );
        }

    }

    private static void onServerStopped(ServerStoppedEvent event) {
        mobDrops.clear();
        mobLootTables.clear();
    }

    public static boolean isValidFabricatedBlock(Block block) {
        return validFabricatedBlocks.contains(block);
    }
    
    public static boolean isValidWhetstoneItem(Item item) {
        return validWhetstoneItems.contains(item);
    }
    
    public static boolean isValidBracingItem(Item item) {
        return validBracingItems.contains(item);
    }
    
    public static boolean isValidDiamondShardItem(Item item) {
        return validDiamondShardItems.contains(item);
    }
    
    public static boolean isValidIronBloomItem(Item item) {
        return validIronBloomItems.contains(item);
    }
    
    public static boolean isValidGoldRepairItem(Item item) {
        return validGoldRepairItems.contains(item);
    }
    
    public static boolean isValidNetheriteRepairItem(Item item) {
        return validNetheriteRepairItems.contains(item);
    }

    public static boolean isMobWardedByItem(Entity mob, Item item) {
        Set<Item> drops = mobDrops.get(mob.getType());
        if (drops == null || drops.isEmpty()) return false;
        return drops.contains(item);
    }

    public static Set<EntityType<?>> getEntityTypesWardedBy(Item item) {
        Set<EntityType<?>> result = new HashSet<>();
        for (Map.Entry<EntityType<?>, Set<Item>> entry : mobDrops.entrySet()) {
            if (entry.getValue().contains(item)) {
                result.add(entry.getKey());
            }
        }
        return result;
    }

    // Reflection fields (cached)
    private static Field POOL_ENTRIES_FIELD;
    private static Field LOOT_ITEM_FIELD;

    static {
        try {
            // Cache reflection fields on class load
            POOL_ENTRIES_FIELD = LootPool.class.getDeclaredField("entries");
            POOL_ENTRIES_FIELD.setAccessible(true);

            // LootItem class is inner class of entries package
            Class<?> lootItemClass = Class.forName("net.minecraft.world.level.storage.loot.entries.LootItem");
            LOOT_ITEM_FIELD = lootItemClass.getDeclaredField("item");
            LOOT_ITEM_FIELD.setAccessible(true);

        } catch (Exception e) {
            LoggerProject.logError("001011", "Failed to initialize reflection fields for loot tables: " + e.getMessage());
        }
    }

    public static void loadEntityLootTables(ResourceLocation id, List<LootPool> pools, LootItemFunction[] functions)
    {
        ResourceLocation loc = extractEntityId(id);
        if(loc==null) return;
        Set<Item> lootItems = mobLootTables.getOrDefault(id, new HashSet<>());

        try {
            for (LootPool pool : pools) {
                LootPoolEntryContainer[] entries = (LootPoolEntryContainer[]) POOL_ENTRIES_FIELD.get(pool);
                if (entries == null || entries.length<1) continue;

                for (LootPoolEntryContainer entry : entries) {
                    if (entry.getClass().getSimpleName().equals("LootItem")) {
                        Item item = (Item) LOOT_ITEM_FIELD.get(entry);
                        if (item != null) lootItems.add(item);
                    }
                }
            }
        } catch (Exception e) {
            LoggerProject.logDebug("001010", "Failed to load loot table for " + id + ": " + e.getMessage());
        }

        if (!lootItems.isEmpty()) {
            mobLootTables.put( loc, lootItems);
        }
    }

    private static ResourceLocation extractEntityId(ResourceLocation lootTableLoc) {
        String path = lootTableLoc.getPath();
        if (!path.startsWith("entities/")) return null;

        String entityPath = path.substring("entities/".length());
        String[] parts = entityPath.split("/");
        String baseEntityName = parts[0];

        return new ResourceLocation(lootTableLoc.getNamespace(), baseEntityName);
    }
}
