package com.holybuckets.traveler.block.be;

import com.holybuckets.traveler.menu.WeatheredBeaconMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        ContainerData access;
        try {
            Field accessField =  BeaconBlockEntity.class.getDeclaredField("dataAccess");
            accessField.setAccessible(true);
            access = (ContainerData) accessField.get(this);
        } catch (Exception e) {
            return null;
        }
        if(access == null) return null;
        return new WeatheredBeaconMenu(
            containerId, playerInventory, access,
            ContainerLevelAccess.create(this.level, this.getBlockPos())
        );
    }

}