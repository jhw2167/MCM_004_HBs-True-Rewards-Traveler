package com.holybuckets.traveler.item;

import com.holybuckets.traveler.core.ManagedTraveler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Soulbound Ritual Tablet - Marks the inventory slot it's used in as soulbound
 * Items in soulbound slots are kept after death
 */
public class WarriorRitualTabletItem extends InteractiveRewardItem {

    public static final String WARRIOR_TAG = "hbs_warrior_slot";

    public WarriorRitualTabletItem() {
        super("warrior_ritual_tablet", true); // Consumes on use
    }

    @Override
    protected InteractionResult onRightClickAir(Level level, Player player, InteractionHand hand, ItemStack stack) {
        // Check if the tablet item is actually in the interaction hand
        ItemStack handStack = player.getItemInHand(hand);
        if (!handStack.is(this)) {
            return InteractionResult.FAIL;
        }
        
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            ManagedTraveler.useWarriorRitualTablet(serverPlayer);
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

}
