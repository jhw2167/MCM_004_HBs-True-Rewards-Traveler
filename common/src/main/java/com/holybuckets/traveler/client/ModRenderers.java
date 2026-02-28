package com.holybuckets.traveler.client;

import com.holybuckets.traveler.Constants;
import com.holybuckets.traveler.block.ModBlocks;
import com.holybuckets.traveler.block.be.ModBlockEntities;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.minecraft.client.renderer.RenderType;
import com.holybuckets.traveler.client.render.WeatheredBeaconRenderer;
import net.minecraft.resources.ResourceLocation;

public class ModRenderers {

    //public static ModelLayerLocation someModel;

    public static void clientInitialize(BalmRenderers renderers) {
        renderers.setBlockRenderType(() -> ModBlocks.weatheredBeacon, RenderType.cutout());
        renderers.registerBlockEntityRenderer( id("weathered_beacon"),
        ModBlockEntities.weatheredBeacon::get, WeatheredBeaconRenderer::new);
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }

}
