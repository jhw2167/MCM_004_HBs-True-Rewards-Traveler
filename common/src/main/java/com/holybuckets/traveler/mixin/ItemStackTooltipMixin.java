package com.holybuckets.traveler.mixin;

import com.holybuckets.traveler.core.ManagedTraveler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackTooltipMixin {

    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void appendDynamicTooltip(Player player, TooltipFlag flag,
                                      CallbackInfoReturnable<List<Component>> cir)
    {
        if(ManagedTraveler.localTraveler==null) return;
        ManagedTraveler.localTraveler.appendLastingTooltip((ItemStack)(Object)this, cir.getReturnValue());
    }
}