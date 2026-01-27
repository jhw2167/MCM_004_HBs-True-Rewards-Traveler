package com.holybuckets.traveler.config;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.traveler.TravelerRewardsMain;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class ModConfig {

    static Set<Block> validFabricatedBlocks = new HashSet<>();
    static Set<Item> validWhetstoneItems = new HashSet<>();
    static Set<Item> validBracingItems = new HashSet<>();
    static Set<Item> validDiamondShardItems = new HashSet<>();
    static Set<Item> validIronBloomItems = new HashSet<>();
    static Set<Item> validGoldRepairItems = new HashSet<>();
    static Set<Item> validNetheriteRepairItems = new HashSet<>();

    public static void init(EventRegistrar reg ) {
        reg.registerOnBeforeServerStarted( ModConfig::onServerStarting, EventPriority.High );
    }

    private static void onServerStarting(ServerStartingEvent event) {
        TravelerRewardsConfig CONFIG = TravelerRewardsMain.CONFIG;
        
        // Convert fabrication blocks
        for( String blockId : CONFIG.fabricationRitualBlocksWhitelist ) {
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
