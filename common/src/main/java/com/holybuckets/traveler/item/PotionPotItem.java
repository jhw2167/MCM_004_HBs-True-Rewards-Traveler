package com.holybuckets.traveler.item;

import com.holybuckets.traveler.menu.PotionPotMenu;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.Random;

/**
 * Potion Pot - Opens a cosmetic brewing stand interface
 * Displays 1-3 awkward potions ready to brew
 */
public class PotionPotItem extends InteractiveRewardItem {

    private static final Random RANDOM = new Random();

    public PotionPotItem() {
        super("potion_pot", false, 1); // Does not consume on use
    }

    @Override
    protected InteractionResult onRightClickAir(Level level, Player player, InteractionHand hand, ItemStack stack) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            openPotionPotMenu(serverPlayer, stack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult onRightClickBlock(UseOnContext context) {
        Player player = context.getPlayer();
        if (player instanceof ServerPlayer serverPlayer) {
            openPotionPotMenu(serverPlayer, context.getItemInHand());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }

    /**
     * Opens the Potion Pot brewing menu
     */
    private void openPotionPotMenu(ServerPlayer player, ItemStack potionPotStack) {
        ItemStack singleStack;
        if (potionPotStack.getCount() > 1) {
            singleStack = potionPotStack.split(1);
            if (potionPotStack.isEmpty()) {
                player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
            }
            player.getInventory().add(potionPotStack); // re-add remainder
        } else {
            singleStack = potionPotStack;
        }

        int awkwardPotionCount;
        if (singleStack.hasTag() && singleStack.getTag().contains("awkward_potion_count")) {
            awkwardPotionCount = singleStack.getTag().getInt("awkward_potion_count");
        } else {
            awkwardPotionCount = RANDOM.nextInt(3) + 1;
        }

        final ItemStack menuStack = singleStack;
        Balm.getNetworking().openMenu(player, new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.hbs_traveler_rewards.potion_pot");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                Container brewingContainer = new SimpleContainer(5);
                return new PotionPotMenu(containerId, playerInventory, menuStack, brewingContainer, awkwardPotionCount);
            }
        });
    }
}