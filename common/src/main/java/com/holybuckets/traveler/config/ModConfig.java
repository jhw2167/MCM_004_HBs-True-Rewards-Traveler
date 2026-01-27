package com.holybuckets.traveler.config;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.traveler.TravelerRewardsMain;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.world.level.block.Block;

import java.util.HashSet;
import java.util.Set;

public class ModConfig {

    static Set<Block> validFabricatedBlocks = new HashSet<>();

    public static void init(EventRegistrar reg ) {
        reg.registerOnBeforeServerStarted( ModConfig::onServerStarting, EventPriority.High );
    }

    private static void onServerStarting(ServerStartingEvent event) {
        TravelerRewardsConfig CONFIG = TravelerRewardsMain.CONFIG;
        for( String blockId : CONFIG.fabricationRitualBlocksWhitelist ) {
            Block block = HBUtil.BlockUtil.blockNameToBlock(blockId);
            if( block != null ) {
                validFabricatedBlocks.add(block);
            }
        }
    }

    public static boolean isValidFabricatedBlock(Block block) {
        return validFabricatedBlocks.contains(block);
    }

}
