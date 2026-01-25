package com.holybuckets.traveler.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

/**
 * Escape Rope - Right-click while at full health to escape the current structure
 * Returns player to a safe location outside the structure
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
    
    private InteractionResult attemptEscape(ServerPlayer player) {
        // Check if player is at full health
        if (player.getHealth() < player.getMaxHealth()) {
            player.sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.escape_rope.not_full_health"));
            return InteractionResult.FAIL;
        }
        
        // TODO: Implement structure detection logic
        // For now, teleport player to spawn or a safe location above ground
        
        ServerLevel level = player.serverLevel();
        BlockPos spawnPos = player.getRespawnPosition();
        
        if (spawnPos == null) {
            spawnPos = level.getSharedSpawnPos();
        }
        
        // Find safe location at or above spawn point
        BlockPos safePos = findSafeLocation(level, spawnPos);
        
        if (safePos != null) {
            player.teleportTo(level, safePos.getX() + 0.5, safePos.getY(), safePos.getZ() + 0.5, player.getYRot(), player.getXRot());
            player.sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.escape_rope.success"));
            return InteractionResult.CONSUME;
        } else {
            player.sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.escape_rope.no_safe_location"));
            return InteractionResult.FAIL;
        }
    }
    
    private BlockPos findSafeLocation(ServerLevel level, BlockPos startPos) {
        // Search upward from start position to find a safe spot
        for (int y = startPos.getY(); y < level.getMaxBuildHeight() - 2; y++) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            BlockPos abovePos = checkPos.above();
            BlockPos twoAbovePos = abovePos.above();
            
            // Check if there's solid ground and 2 blocks of air above
            if (level.getBlockState(checkPos).isSolidRender(level, checkPos) &&
                level.getBlockState(abovePos).isAir() &&
                level.getBlockState(twoAbovePos).isAir()) {
                return abovePos;
            }
        }
        
        return null;
    }
}
