package com.holybuckets.traveler.mixin;

import com.holybuckets.traveler.block.ModBlocks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeaconMenu.class)
public abstract class BeaconMenuMixin {

    @Shadow
    @Final
    private ContainerLevelAccess access;

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    private void onStillValid(Player player, CallbackInfoReturnable<Boolean> cir) {
        // Check if the block at this location is a weathered beacon
        boolean isWeatheredBeacon = this.access.evaluate((level, pos) ->
            level.getBlockState(pos).is(ModBlocks.weatheredBeacon), false);
        if (isWeatheredBeacon) {
            boolean valid = this.access.evaluate((level, pos) ->
                    player.distanceToSqr((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) <= 64.0,
                true
            );
            cir.setReturnValue(valid);
        }
    }
}