package com.holybuckets.traveler.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;

/**
 * Fabrication Ritual Tablet - Duplicates the contents of a chest
 * Right-click on a chest to duplicate its contents
 */
public class FabricationRitualTabletItem extends InteractiveRewardItem {

    public FabricationRitualTabletItem() {
        super("fabrication_ritual_tablet", true); // Consumes on use
    }

    @Override
    protected InteractionResult onRightClickBlock(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (blockEntity instanceof ChestBlockEntity chestEntity) {
                return duplicateChestContents(context, chestEntity);
            } else if (blockEntity instanceof Container container) {
                return duplicateContainerContents(context, container);
            } else {
                if (context.getPlayer() != null) {
                    context.getPlayer().sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.invalid_target"));
                }
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult duplicateChestContents(UseOnContext context, ChestBlockEntity chest) {
        // Duplicate all items in the chest
        int duplicatedCount = 0;

        for (int i = 0; i < chest.getContainerSize(); i++) {
            ItemStack stack = chest.getItem(i);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                // Try to add the duplicated item to an empty slot
                for (int j = 0; j < chest.getContainerSize(); j++) {
                    ItemStack targetStack = chest.getItem(j);
                    if (targetStack.isEmpty()) {
                        chest.setItem(j, copy);
                        duplicatedCount++;
                        break;
                    } else if (ItemStack.isSameItemSameTags(targetStack, copy)) {
                        int space = targetStack.getMaxStackSize() - targetStack.getCount();
                        if (space > 0) {
                            int toAdd = Math.min(space, copy.getCount());
                            targetStack.grow(toAdd);
                            copy.shrink(toAdd);
                            if (copy.isEmpty()) {
                                duplicatedCount++;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (context.getPlayer() != null) {
            if (duplicatedCount > 0) {
                context.getPlayer().sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.success", duplicatedCount));
                return InteractionResult.CONSUME;
            } else {
                context.getPlayer().sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.chest_full"));
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.FAIL;
    }

    private InteractionResult duplicateContainerContents(UseOnContext context, Container container) {
        // Generic container duplication logic
        int duplicatedCount = 0;

        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                for (int j = 0; j < container.getContainerSize(); j++) {
                    ItemStack targetStack = container.getItem(j);
                    if (targetStack.isEmpty()) {
                        container.setItem(j, copy);
                        duplicatedCount++;
                        break;
                    } else if (ItemStack.isSameItemSameTags(targetStack, copy)) {
                        int space = targetStack.getMaxStackSize() - targetStack.getCount();
                        if (space > 0) {
                            int toAdd = Math.min(space, copy.getCount());
                            targetStack.grow(toAdd);
                            copy.shrink(toAdd);
                            if (copy.isEmpty()) {
                                duplicatedCount++;
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (context.getPlayer() != null) {
            if (duplicatedCount > 0) {
                context.getPlayer().sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.success", duplicatedCount));
                return InteractionResult.CONSUME;
            } else {
                context.getPlayer().sendSystemMessage(Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.chest_full"));
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.FAIL;
    }
}