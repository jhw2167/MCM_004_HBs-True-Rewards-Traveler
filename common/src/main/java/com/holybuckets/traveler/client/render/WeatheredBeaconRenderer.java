package com.holybuckets.traveler.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.holybuckets.traveler.block.be.WeatheredBeaconBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;

public class WeatheredBeaconRenderer implements BlockEntityRenderer<WeatheredBeaconBlockEntity> {

    // Dull, weathered beam color - desaturated greenish-gray instead of pure white
    private static final float[] WEATHERED_BEAM_COLOR = new float[] { 0.55f, 0.65f, 0.55f };

    // Narrower beam to appear weaker
    private static final float INNER_RADIUS = 0.1f;  // vanilla is 0.2f
    private static final float OUTER_RADIUS = 0.15f; // vanilla is 0.25f

    public WeatheredBeaconRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WeatheredBeaconBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        long gameTime = blockEntity.getLevel().getGameTime();
        var beamSections = blockEntity.getBeamSections();

        int yOffset = 0;
        for (int i = 0; i < beamSections.size(); i++) {
            BeaconBlockEntity.BeaconBeamSection section = beamSections.get(i);
            int height = i == beamSections.size() - 1 ? BeaconRenderer.MAX_RENDER_Y : section.getHeight();
            float[] sectionColor = section.getColor();
            float[] blendedColor = new float[] {
                Math.min(1.0f, (WEATHERED_BEAM_COLOR[0] + sectionColor[0]) / 2.0f),
                Math.min(1.0f, (WEATHERED_BEAM_COLOR[1] + sectionColor[1]) / 2.0f),
                Math.min(1.0f, (WEATHERED_BEAM_COLOR[2] + sectionColor[2]) / 2.0f)
            };

            BeaconRenderer.renderBeaconBeam(
                poseStack,
                bufferSource,
                BeaconRenderer.BEAM_LOCATION,
                partialTick,
                1.0f,
                gameTime,
                yOffset,
                height,
                blendedColor,
                INNER_RADIUS,
                OUTER_RADIUS
            );

            yOffset += section.getHeight();
        }
    }

    @Override
    public boolean shouldRenderOffScreen(WeatheredBeaconBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}