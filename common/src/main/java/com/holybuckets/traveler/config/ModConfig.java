package com.holybuckets.traveler.config;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.traveler.TravelerRewardsMain;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModConfig {

    public static Set<Block> validFabricatedBlocks = new HashSet<>();
    public static Set<Item> validWhetstoneItems = new HashSet<>();
    public static Set<Item> validBracingItems = new HashSet<>();
    public static Set<Item> validDiamondShardItems = new HashSet<>();
    public static Set<Item> validIronBloomItems = new HashSet<>();
    public static Set<Item> validGoldRepairItems = new HashSet<>();
    public static Set<Item> validNetheriteRepairItems = new HashSet<>();

    public static final Map<EntityType<?>, Set<Item>> mobDrops = new HashMap<>();

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
        for( EntityType<?> entityType : entities ) {
            Set<Item> drops = new HashSet<>();
            for( String itemId : CONFIG.mobDrops.getMobDropItems( Registry.ENTITY_TYPE.getKey(entityType).toString() ) ) {
                Item item = HBUtil.ItemUtil.itemNameToItem(itemId);
                if( item != null ) {
                    drops.add(item);
                }
            }
            if( !drops.isEmpty() ) {
                mobDrops.put( entityType, drops );
            }
        }

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

}
