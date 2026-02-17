// WeatheredBeaconMenu.java
package com.holybuckets.traveler.menu;

import com.holybuckets.traveler.block.ModBlocks;
import com.holybuckets.traveler.menu.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Optional;

public class WeatheredBeaconMenu extends BeaconMenu {
    private static final int PAYMENT_SLOT = 0;
    private static final int SLOT_COUNT = 1;
    private static final int DATA_COUNT = 2;
    private static final int INV_SLOT_START = 1;
    private static final int INV_SLOT_END = 28;
    private static final int USE_ROW_SLOT_START = 28;
    private static final int USE_ROW_SLOT_END = 37;

    private Container beacon;
    private final Slot paymentSlot;
    private final ContainerLevelAccess access;
    private final ContainerData beaconData;

    public WeatheredBeaconMenu(int containerId, Container playerInventory, FriendlyByteBuf friendlyByteBuf) {
        this(containerId, playerInventory, new SimpleContainerData(3), ContainerLevelAccess.NULL);
    }

    public WeatheredBeaconMenu(int containerId, Container playerInventory, ContainerData beaconData, ContainerLevelAccess access) {
        super(containerId, playerInventory, beaconData,  access);
        //use reflection to access final private field beacon from superclass
        try {
            Field beaconField = BeaconMenu.class.getDeclaredField("beacon");
            beaconField.setAccessible(true);
            this.beacon = (Container) beaconField.get(this);
        } catch (Exception e) {
            this.beacon = new SimpleContainer(1) {
                public boolean canPlaceItem(int slot, ItemStack stack) {
                    return stack.is(ItemTags.BEACON_PAYMENT_ITEMS);
                }

                public int getMaxStackSize() {
                    return 1;
                }
            };
        }


        this.beaconData = beaconData;
        this.access = access;
        this.paymentSlot = getSlot(0);
        //this.addSlot(this.paymentSlot);
        //this.addDataSlots(beaconData);
    }

    @Override
    public MenuType<?> getType() {
        return ModMenus.WEATHERED_BEACON.get();
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
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.weatheredBeacon);
    }

    @Override
    public void setData(int id, int value) {
        if(id>1) return;
        super.setData(id, value);
        this.broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack resultStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            resultStack = slotStack.copy();

            if (slotIndex == 0) {
                // Moving from payment slot to inventory
                if (!this.moveItemStackTo(slotStack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, resultStack);
            } else {
                // Moving from inventory to payment slot
                if (this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }

                if (slotIndex >= 1 && slotIndex < 28) {
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
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == resultStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return resultStack;
    }

    public int getLevels() {
        return this.beaconData.get(0);
    }

    @Nullable
    public MobEffect getPrimaryEffect() {
        return MobEffect.byId(this.beaconData.get(1));
    }

    @Override
    @Nullable
    public MobEffect getSecondaryEffect() {
        return getPrimaryEffect();
    }

    @Override
    public void updateEffects(Optional<MobEffect> primary, Optional<MobEffect> secondary) {
        if (this.paymentSlot.hasItem()) {
            this.beaconData.set(1, primary.map(MobEffect::getId).orElse(-1));
            this.beaconData.set(2, primary.map(MobEffect::getId).orElse(-1));
            this.paymentSlot.remove(1);
            this.access.execute(Level::blockEntityChanged);
        }
    }

    @Override
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