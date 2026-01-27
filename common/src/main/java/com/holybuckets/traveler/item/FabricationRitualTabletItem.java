package com.holybuckets.traveler.item;

import com.holybuckets.traveler.config.ModConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

import static com.holybuckets.foundation.CommonClass.MESSAGER;

/**
 * Fabrication Ritual Tablet - Duplicates a chest and its valid contents
 * Right-click on a single chest to create a duplicate adjacent chest
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
                // Check if it's a single chest (not double)
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() instanceof ChestBlock) {
                    ChestType chestType = state.getValue(ChestBlock.TYPE);

                    if (chestType != ChestType.SINGLE) {
                        MESSAGER.sendBottomActionHint(
                            Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.not_single_chest").getString()
                        );
                        return InteractionResult.FAIL;
                    }
                }

                return fabricateChest(context, chestEntity, pos, level);
            } else {
                MESSAGER.sendBottomActionHint(
                    Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.invalid_target").getString()
                );
                return InteractionResult.FAIL;
            }
        }

        return InteractionResult.SUCCESS;
    }

    private InteractionResult fabricateChest(UseOnContext context, ChestBlockEntity sourceChest, BlockPos sourcePos, Level level) {
        // Try to find a valid adjacent position for the new chest
        BlockPos newChestPos = findAdjacentSpace(level, sourcePos);

        if (newChestPos == null) {
            MESSAGER.sendBottomActionHint(
                Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.no_space").getString()
            );
            return InteractionResult.FAIL;
        }

        // Place the new chest
        BlockState chestState = level.getBlockState(sourcePos);
        level.setBlock(newChestPos, chestState, 3);

        // Get the new chest block entity
        BlockEntity newBlockEntity = level.getBlockEntity(newChestPos);
        if (!(newBlockEntity instanceof ChestBlockEntity newChest)) {
            return InteractionResult.FAIL;
        }

        // Copy valid items slot by slot
        int copiedCount = 0;
        for (int i = 0; i < sourceChest.getContainerSize(); i++) {
            ItemStack sourceStack = sourceChest.getItem(i);

            if (!sourceStack.isEmpty()) {
                // Check if item is valid for fabrication
                if (isValidFabricationItem(sourceStack)) {
                    ItemStack copy = sourceStack.copy();
                    newChest.setItem(i, copy); // Copy to same slot position
                    copiedCount++;
                }
            }
        }

        // Success message
        MESSAGER.sendBottomActionHint(
            Component.translatable("item.hbs_traveler_rewards.fabrication_ritual_tablet.success", copiedCount).getString()
        );

        return InteractionResult.CONSUME;
    }

    /**
     * Finds an adjacent empty space to place the new chest
     * Checks in order: left, right, front, back, top
     *
     * @return BlockPos of valid space, or null if none found
     */
    private BlockPos findAdjacentSpace(Level level, BlockPos pos) {
        // Check all horizontal directions + up
        Direction[] directions = {
            Direction.WEST,  // Left
            Direction.EAST,  // Right
            Direction.NORTH, // Front (in terms of negative Z)
            Direction.SOUTH, // Back
            Direction.UP     // Top
        };

        for (Direction dir : directions) {
            BlockPos checkPos = pos.relative(dir);
            BlockState state = level.getBlockState(checkPos);

            // Check if position is air or replaceable (like grass, water, etc.)
            if (state.isAir() || state.canBeReplaced()) {
                // Also check if there's a solid block below (except when placing on top)
                if (dir == Direction.UP) {
                    // For top placement, the source chest is the support
                    return checkPos;
                } else {
                    // For side placements, check if there's ground below
                    BlockPos belowPos = checkPos.below();
                    BlockState belowState = level.getBlockState(belowPos);
                    if (!belowState.isAir() && belowState.isSolidRender(level, belowPos)) {
                        return checkPos;
                    }
                }
            }
        }

        return null; // No valid space found
    }

    /**
     * Checks if an item is valid for fabrication
     * Must be a BlockItem and the block must be validated by ModConfig
     *
     * @param stack The item stack to check
     * @return true if valid for fabrication
     */
    private boolean isValidFabricationItem(ItemStack stack) {
        // Check #1: Must be a BlockItem
        if (!(stack.getItem() instanceof BlockItem blockItem)) {
            return false;
        }

        // Check #2: Block must be validated by ModConfig
        Block block = blockItem.getBlock();
        return ModConfig.isValidFabricatedBlock(block);
    }
}
//END CLASS