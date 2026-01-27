package com.holybuckets.traveler.item;

import com.holybuckets.traveler.core.ManagedTraveler;
import static com.holybuckets.foundation.CommonClass.MESSAGER;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;


/**
 * Escape Rope - Teleports player to structure entry point
 * Requirements:
 * - Must be at full health
 * - Must be inside a tracked structure
 */
public class EscapeRopeItem extends InteractiveRewardItem {

    public EscapeRopeItem() {
        super("escape_rope", true); // Consumes on use
    }

    @Override
    protected InteractionResult onRightClickAir(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            return attemptEscape(serverPlayer);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    private InteractionResult attemptEscape(ServerPlayer player)
    {
        // Check if player is at full health
        if (player.getHealth() < player.getMaxHealth()) {
            MESSAGER.sendBottomActionHint(
                Component.translatable("item.hbs_traveler_rewards.escape_rope.not_full_health").getString()
            );
            return InteractionResult.FAIL;
        }

        // Get player's traveler data
        ManagedTraveler traveler = ManagedTraveler.getManagedTraveler(player);
        if (traveler == null) {
            MESSAGER.sendBottomActionHint(
                Component.translatable("item.hbs_traveler_rewards.escape_rope.failed").getString()
            );
            return InteractionResult.FAIL;
        }

        // Check if player is in a structure
        if ( !traveler.isInStructure() && !traveler.isInDeepCaves())
        {
            MESSAGER.sendBottomActionHint(
                Component.translatable("item.hbs_traveler_rewards.escape_rope.not_in_structure").getString()
            );
            return InteractionResult.FAIL;
        }

        traveler.onUseEscapeRope();

        // Success message
        MESSAGER.sendBottomActionHint(
            Component.translatable("item.hbs_traveler_rewards.escape_rope.success").getString()
        );

        return InteractionResult.CONSUME;
    }
}