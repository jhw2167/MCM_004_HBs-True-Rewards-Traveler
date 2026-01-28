package com.holybuckets.traveler.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

/**
 * Menu for Mob Ward item
 * Single slot for filtering items, displays currently warded mobs
 */
public class MobWardMenu extends AbstractContainerMenu {

    private final ItemStack mobWardStack;
    private Component wardMessage = Component.empty();

    // Client-side constructor
    public MobWardMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.getSelected());
    }

    // Server-side constructor
    public MobWardMenu(int containerId, Inventory playerInventory, ItemStack mobWardStack) {
        super(ModMenus.MOB_WARD_MENU.get(), containerId);
        this.mobWardStack = mobWardStack;

        // Single slot for item (centered at position 80, 35)
        this.addSlot(new Slot(new MobWardContainer(mobWardStack), 0, 80, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return true; // Accept any item
            }

            @Override
            public int getMaxStackSize() {
                return 1; // Only one item
            }
        });

        // Add player inventory slots
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        // Initialize ward message
        updateWardMessage();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            if (index == 0) {
                // Moving from ward slot to inventory
                if (!this.moveItemStackTo(slotStack, 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Moving from inventory to ward slot
                if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getInventory().contains(mobWardStack);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);

        // Save the item in slot back to ward's NBT
        if (!player.level().isClientSide()) {
            ItemStack filterItem = this.slots.get(0).getItem();
            MobWardContainer.saveFilterItem(mobWardStack, filterItem);
        }
    }

    /**
     * Update the ward message based on current NBT data
     */
    public void updateWardMessage() {
        // Read warded mobs from NBT
        if (mobWardStack.hasTag() && mobWardStack.getTag().contains("WardedMobs")) {
            String wardedMobs = mobWardStack.getTag().getString("WardedMobs");
            if (!wardedMobs.isEmpty()) {
                this.wardMessage = Component.literal("Warding: " + wardedMobs);
            } else {
                this.wardMessage = Component.literal("No mobs currently warded");
            }
        } else {
            this.wardMessage = Component.literal("No mobs currently warded");
        }
    }

    /**
     * Get the current ward message for display
     */
    public Component getWardMessage() {
        return wardMessage;
    }

    /**
     * Set the ward message (called from server)
     */
    public void setWardMessage(Component message) {
        this.wardMessage = message;
    }

    // Helper methods to add player inventory
    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    /**
     * Simple container that stores one item in the Mob Ward's NBT
     */
    private static class MobWardContainer implements net.minecraft.world.Container {
        private final ItemStack mobWard;
        private ItemStack filterItem = ItemStack.EMPTY;

        public MobWardContainer(ItemStack mobWard) {
            this.mobWard = mobWard;
            loadFilterItem();
        }

        private void loadFilterItem() {
            if (mobWard.hasTag() && mobWard.getTag().contains("FilterItem")) {
                filterItem = ItemStack.of(mobWard.getTag().getCompound("FilterItem"));
            }
        }

        public static void saveFilterItem(ItemStack mobWard, ItemStack filterItem) {
            if (!mobWard.hasTag()) {
                mobWard.setTag(new net.minecraft.nbt.CompoundTag());
            }

            if (filterItem.isEmpty()) {
                mobWard.getTag().remove("FilterItem");
            } else {
                mobWard.getTag().put("FilterItem", filterItem.save(new net.minecraft.nbt.CompoundTag()));
            }
        }

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return filterItem.isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            return slot == 0 ? filterItem : ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            if (slot == 0 && !filterItem.isEmpty()) {
                ItemStack result = filterItem.split(amount);
                if (filterItem.isEmpty()) {
                    filterItem = ItemStack.EMPTY;
                }
                setChanged();
                return result;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            if (slot == 0) {
                ItemStack result = filterItem;
                filterItem = ItemStack.EMPTY;
                return result;
            }
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            if (slot == 0) {
                filterItem = stack;
                setChanged();
            }
        }

        @Override
        public void setChanged() {
            // Container changed
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public void clearContent() {
            filterItem = ItemStack.EMPTY;
        }
    }
}