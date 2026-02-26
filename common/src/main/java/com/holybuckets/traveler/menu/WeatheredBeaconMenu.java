package com.holybuckets.traveler.menu;

import com.holybuckets.traveler.LoggerProject;
import com.holybuckets.traveler.block.ModBlocks;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;

public class WeatheredBeaconMenu extends AbstractContainerMenu {

    private static final int DATA_COUNT = 2; // levels + primary effect only (no secondary)

    private final Container beacon;
    private final PaymentSlot paymentSlot;
    private final ContainerLevelAccess access;
    private final ContainerData beaconData;

    // Client-side deserializing constructor - called when server opens the menu on client
    public WeatheredBeaconMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        this(containerId, playerInventory, new SimpleContainerData(DATA_COUNT), ContainerLevelAccess.NULL);
    }

    // Server-side constructor - called by the block entity's menu provider
    public WeatheredBeaconMenu(int containerId, Inventory playerInventory, ContainerData beaconData, ContainerLevelAccess access) {
        super(ModMenus.WEATHERED_BEACON.get(), containerId);

        checkContainerDataCount(beaconData, DATA_COUNT);
        this.beaconData = beaconData;
        this.access = access;

        this.beacon = new SimpleContainer(1) {
            @Override
            public boolean canPlaceItem(int slot, ItemStack stack) {
                return stack.is(ItemTags.BEACON_PAYMENT_ITEMS);
            }

            @Override
            public int getMaxStackSize() {
                return 1;
            }
        };

        this.paymentSlot = new PaymentSlot(this.beacon, 0, 136, 110);
        this.addSlot(this.paymentSlot);
        this.addDataSlots(beaconData);

        // Player inventory (3 rows)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 36 + col * 18, 137 + row * 18));
            }
        }

        // Hotbar
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 36 + col * 18, 195));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.weatheredBeacon);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide) {
            ItemStack paymentItem = this.paymentSlot.remove(this.paymentSlot.getMaxStackSize());
            if (!paymentItem.isEmpty()) {
                player.drop(paymentItem, false);
            }
        }
    }

    @Override
    public void setData(int index, int value) {
        if(index>1) return;
        super.setData(index, value);
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            if (slotIndex == 0) {
                if (!this.moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, result);
            } else if (!this.paymentSlot.hasItem() && this.paymentSlot.mayPlace(slotStack) && slotStack.getCount() == 1) {
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 1 && slotIndex < 28) {
                if (!this.moveItemStackTo(slotStack, 28, 37, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 28 && slotIndex < 37) {
                if (!this.moveItemStackTo(slotStack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, 1, 37, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return result;
    }

    public int getLevels() {
        return this.beaconData.get(0);
    }

    @Nullable
    public MobEffect getPrimaryEffect() {
        return MobEffect.byId(this.beaconData.get(1));
    }

    // Weathered beacon only has one effect tier - secondary mirrors primary
    @Nullable
    public MobEffect getSecondaryEffect() {
        return getPrimaryEffect();
    }

    public void updateEffects(Optional<MobEffect> primary, Optional<MobEffect> secondary) {
        if (this.paymentSlot.hasItem()) {
            this.beaconData.set(1, primary.map(MobEffect::getId).orElse(-1));
            this.beaconData.set(2, primary.map(MobEffect::getId).orElse(-1));
            this.paymentSlot.remove(1);
            this.access.execute(Level::blockEntityChanged);
        }
    }

    public boolean hasPayment() {
        return !this.beacon.getItem(0).isEmpty();
    }

    class PaymentSlot extends Slot {
        public PaymentSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.is(ItemTags.BEACON_PAYMENT_ITEMS);
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }
    }
}