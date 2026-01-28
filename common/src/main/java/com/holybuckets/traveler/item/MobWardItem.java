package com.holybuckets.traveler.item;

import com.holybuckets.traveler.menu.MobWardMenu;
import com.holybuckets.traveler.menu.ModMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/**
 * Mob Ward - Opens a menu to configure which mobs to ward against
 * Place an item in the slot to ward against mobs that drop or are associated with that item
 */
public class MobWardItem extends InteractiveRewardItem {

    public MobWardItem() {
        super("mob_ward", false); // Does not consume on use
    }

    @Override
    protected InteractionResult onRightClickAir(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            openMobWardMenu(serverPlayer, stack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult onRightClickBlock(UseOnContext context) {
        Player player = context.getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            openMobWardMenu(serverPlayer, context.getItemInHand());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    /**
     * Opens the Mob Ward configuration menu
     */
    private void openMobWardMenu(ServerPlayer player, ItemStack mobWardStack) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.hbs_traveler_rewards.mob_ward");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                return new MobWardMenu(containerId, playerInventory, mobWardStack);
            }
        });
    }
}