package com.holybuckets.traveler.client;

import com.holybuckets.traveler.block.ModBlocks;
import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.minecraft.client.renderer.RenderType;

public class ModRenderers {

    //public static ModelLayerLocation someModel;

    public static void clientInitialize(BalmRenderers renderers) {
        //waystoneModel = renderers.registerModel(new ResourceLocation(Waystones.MOD_ID, "waystone"), () -> WaystoneModel.createLayer(CubeDeformation.NONE));
        renderers.setBlockRenderType(() -> ModBlocks.weatheredBeacon, RenderType.cutout());
    }

}
