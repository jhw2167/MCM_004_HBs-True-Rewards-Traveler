package com.holybuckets.traveler.item;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class PureHeartItem extends InteractiveRewardItem {

    private static final UUID PURE_HEART_MODIFIER_UUID = UUID.fromString("a3d89f7e-5c8d-4f3a-9b2e-1d4c6e8f0a1b");
    private static final String PURE_HEART_MODIFIER_NAME = "Pure Heart";
    private static final double HEALTH_PER_HEART = 2.0;

    public PureHeartItem() {
        super("pure_heart", true); // Consumes on use
    }

    @Override
    protected InteractionResult onRightClickAir(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            return increaseHearts(serverPlayer);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    private InteractionResult increaseHearts(ServerPlayer player) {
        AttributeInstance healthAttribute = player.getAttribute(Attributes.MAX_HEALTH);

        if (healthAttribute == null) {
            return InteractionResult.FAIL;
        }

        // Check if player already has the modifier (for stacking multiple pure hearts)
        AttributeModifier existingModifier = healthAttribute.getModifier(PURE_HEART_MODIFIER_UUID);

        double currentBonus = existingModifier != null ? existingModifier.getAmount() : 0.0;
        double newBonus = currentBonus + HEALTH_PER_HEART;

        // Remove existing modifier if present
        if (existingModifier != null) {
            healthAttribute.removeModifier(PURE_HEART_MODIFIER_UUID);
        }

        // Add new modifier with increased health
        AttributeModifier newModifier = new AttributeModifier(
            PURE_HEART_MODIFIER_UUID,
            PURE_HEART_MODIFIER_NAME,
            newBonus,
            AttributeModifier.Operation.ADDITION
        );

        healthAttribute.addPermanentModifier(newModifier);

        // Heal player to new max health
        player.setHealth(player.getMaxHealth()+1);

        // Send feedback message
        int totalHearts = (int) (newBonus / HEALTH_PER_HEART);
        player.sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.pure_heart.success", totalHearts));

        return InteractionResult.CONSUME;
    }
}