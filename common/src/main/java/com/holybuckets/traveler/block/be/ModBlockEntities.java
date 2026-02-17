
package com.holybuckets.traveler.block.be;

import com.holybuckets.traveler.Constants;
import com.holybuckets.traveler.block.ModBlocks;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.block.BalmBlockEntities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    public static DeferredObject<BlockEntityType<WeatheredBeaconBlockEntity>> weatheredBeacon;

    public static void initialize(BalmBlockEntities blockEntities)
    {
        weatheredBeacon = blockEntities.registerBlockEntity( id("weathered_beacon"), WeatheredBeaconBlockEntity::new, () -> new Block[]{ModBlocks.weatheredBeacon} );

    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
