package com.holybuckets.traveler.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;

/**
 * Potion Pot - Cooks potions, breaks on use
 * Art Inspiration: A pot that looks like it could cook some potions
 * 
 * Note: Actual brewing logic to be implemented based on game design requirements
 */
public class PotionPotItem extends InteractiveRewardItem {
    
    public PotionPotItem() {
        super("potion_pot", true); // Breaks (consumes) on use
    }
    
    @Override
    protected InteractionResult onRightClickBlock(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        
        if (!level.isClientSide()) {
            // TODO: Implement potion brewing logic
            // For now, just send a message
            if (context.getPlayer() != null) {
                context.getPlayer().sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.potion_pot.brewing"));
            }
            
            // The pot breaks after use (consumed by the consumeOnUse flag)
            return InteractionResult.CONSUME;
        }
        
        return InteractionResult.SUCCESS;
    }
}
