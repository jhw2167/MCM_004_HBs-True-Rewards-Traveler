package com.holybuckets.traveler.item;

import com.holybuckets.traveler.menu.PotionPotMenu;
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
        super("potion_pot", false, 16); // Does not consume on use
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
        // Get or generate awkward potion count
        int awkwardPotionCount;
        if (potionPotStack.hasTag() && potionPotStack.getTag().contains("AwkwardPotionCount")) {
            awkwardPotionCount = potionPotStack.getTag().getInt("AwkwardPotionCount");
        } else {
            awkwardPotionCount = RANDOM.nextInt(3) + 1;
        }

        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("container.hbs_traveler_rewards.potion_pot");
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
                Container brewingContainer = new SimpleContainer(5);
                return new PotionPotMenu(containerId, playerInventory, potionPotStack, brewingContainer, awkwardPotionCount);
            }
        });
    }
}