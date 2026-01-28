package com.holybuckets.traveler.item;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for items that have right-click interactions with blocks, entities, or the player themselves.
 * Handles consumption logic and provides hooks for subclass behavior.
 *
 * Examples: Pure Heart, Soulbound Ritual Tablet, Fabrication Ritual Tablet, Potion Pot, Escape Rope
 */
public abstract class InteractiveRewardItem extends Item {

    private final String itemId;
    private final boolean consumeOnUse;

    public InteractiveRewardItem(String itemId) {
        this(itemId, true);
    }

    public InteractiveRewardItem(String itemId, boolean consumeOnUse) {
        super(Balm.getItems().itemProperties());
        this.itemId = itemId;
        this.consumeOnUse = consumeOnUse;
    }

    public InteractiveRewardItem(String itemId, boolean consumeOnUse, int maxStackSize) {
        super(Balm.getItems().itemProperties().stacksTo(maxStackSize));
        this.itemId = itemId;
        this.consumeOnUse = consumeOnUse;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.translatable("item.hbs_traveler_rewards." + itemId + ".desc"));
    }

    /**
     * Called when the player right-clicks in the air with this item
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        InteractionResult result = onRightClickAir(level, player, hand, stack);

        if (result.consumesAction() && consumeOnUse && !player.isCreative()) {
            stack.shrink(1);
        }

        return new InteractionResultHolder<>(result, stack);
    }

    /**
     * Called when the player right-clicks on a block with this item
     */
    @Override
    public InteractionResult useOn(UseOnContext context) {
        InteractionResult result = onRightClickBlock(context);

        if (result.consumesAction() && consumeOnUse && !context.getPlayer().isCreative()) {
            context.getItemInHand().shrink(1);
        }

        return result;
    }

    /**
     * Override this to handle right-click in air interactions
     */
    protected InteractionResult onRightClickAir(Level level, Player player, InteractionHand hand, ItemStack stack) {
        return InteractionResult.PASS;
    }

    /**
     * Override this to handle right-click on block interactions
     */
    protected InteractionResult onRightClickBlock(UseOnContext context) {
        return InteractionResult.PASS;
    }

    public String getItemId() {
        return itemId;
    }

    public boolean shouldConsumeOnUse() {
        return consumeOnUse;
    }
}