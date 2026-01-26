package com.holybuckets.traveler.item;

import com.holybuckets.traveler.core.ManagedTraveler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class PureHeartItem extends InteractiveRewardItem {

    public PureHeartItem() {
        super("pure_heart", true); // Consumes on use
    }

    @Override
    protected InteractionResult onRightClickAir(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ManagedTraveler.usePureHeart(serverPlayer);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

}