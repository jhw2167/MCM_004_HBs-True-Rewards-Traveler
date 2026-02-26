package com.holybuckets.traveler.block.be;

import com.holybuckets.traveler.LoggerProject;
import com.holybuckets.traveler.menu.WeatheredBeaconMenu;
import com.holybuckets.traveler.mixin.BeaconBlockEntityAccessor;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class WeatheredBeaconBlockEntity extends BeaconBlockEntity {

    public static final String MSG_ID_UPDATE_EFFECTS = "traveler_update_beacon_effects";
    // Pyramid level ranges: level 1 = 32, level 2 = 32, level 3 = 64, level 4 = 64
    private static final int[] LEVEL_RANGES = {32, 32, 64, 64};

    public WeatheredBeaconBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntities.weatheredBeacon.get();
    }

    // Override the tick method to customize behavior
    public static void tick(Level level, BlockPos pos, BlockState state, BeaconBlockEntity blockEntity) {
        BeaconBlockEntity.tick(level, pos, state, blockEntity);
    }

    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.translatable("block.hbs_traveler_rewards.weathered_beacon");
            }

            @Override
            public AbstractContainerMenu createMenu(int syncId, Inventory playerInventory, Player player) {

                try {
                    ContainerData dataAccess = ((BeaconBlockEntityAccessor) WeatheredBeaconBlockEntity.this).getDataAccess();
                    Level level = WeatheredBeaconBlockEntity.this.level;
                    BlockPos pos = WeatheredBeaconBlockEntity.this.getBlockPos();
                    return new WeatheredBeaconMenu(syncId, playerInventory, dataAccess, ContainerLevelAccess.create(level, pos));
                } catch (Exception e) {
                    throw new RuntimeException("Failed to access beacon dataAccess field", e);
                }
                //return new BeaconMenu(syncId, playerInventory, dataAccess, ContainerLevelAccess.create(level, pos));
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {

            }
        };
    }

}