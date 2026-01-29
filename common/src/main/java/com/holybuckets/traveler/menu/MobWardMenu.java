package com.holybuckets.traveler.menu;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.traveler.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;

import java.util.Set;

import static com.holybuckets.foundation.HBUtil.EntityUtil.entityTypeToCommonName;

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

            @Override
            public void setChanged() {
                super.setChanged();
                // Save to NBT whenever item changes
                saveFilterItemToNBT();
            }
        });

        // Add player inventory slots
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        // Initialize ward message
        updateWardMessage();
    }

    /**
     * Save the filter item to the Mob Ward's NBT
     */
    private void saveFilterItemToNBT() {
        ItemStack filterItem = this.slots.get(0).getItem();
        MobWardContainer.saveFilterItem(mobWardStack, filterItem);
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

        // Save the item in slot back to ward's NBT when menu closes
        if (!player.level().isClientSide()) {
            saveFilterItemToNBT();
        }
    }

    /**
     * Update the ward message based on current NBT data
     */
    public void updateWardMessage()
    {
        // Read warded mob type from NBT
        if (mobWardStack.hasTag() && mobWardStack.getTag().contains("WardedMobType")) {
            String wardedMobType = mobWardStack.getTag().getString("WardedMobType");
            if (!wardedMobType.isEmpty()) {
                String displayName = formatMobName(wardedMobType);
                this.wardMessage = Component.literal("Warding: " + displayName);
            } else {
                this.wardMessage = Component.literal("No mobs currently warded");
            }
        } else if (mobWardStack.hasTag() && mobWardStack.getTag().contains("filterItem")) {
            // Show filter item if no mob type derived yet
            ItemStack filterItem = ItemStack.of(mobWardStack.getTag().getCompound("filterItem"));
            if (!filterItem.isEmpty()) {
                this.wardMessage = Component.literal("Item: " + filterItem.getDisplayName().getString());
            } else {
                this.wardMessage = Component.literal("No mobs currently warded");
            }
        } else {
            this.wardMessage = Component.literal("No mobs currently warded");
        }
    }

    /**
     * Format mob type name for display
     */
    private String formatMobName(String mobType) {
        String[] parts = mobType.split(":");
        String name = parts.length > 1 ? parts[1] : parts[0];
        name = name.replace("_", " ");

        // Capitalize each word
        String[] words = name.split(" ");
        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                formatted.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1))
                    .append(" ");
            }
        }
        return formatted.toString().trim();
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

        /**
         * Load filter item from Mob Ward's NBT
         */
        private void loadFilterItem() {
            if (mobWard.hasTag() && mobWard.getTag().contains("filterItem")) {
                filterItem = ItemStack.of(mobWard.getTag().getCompound("filterItem"));
            }
        }

        /**
         * Save filter item to Mob Ward's NBT
         */
        public static void saveFilterItem(ItemStack mobWard, ItemStack filterItem) {
            CompoundTag tag = mobWard.getOrCreateTag();

            if (filterItem.isEmpty()) {
                // Remove filter item
                tag.remove("filterItem");
                tag.remove("WardedMobType");
            } else {
                // Save filter item
                CompoundTag itemTag = new CompoundTag();
                filterItem.save(itemTag);
                tag.put("filterItem", itemTag);

                // Also derive and save mob type
                String mobType = getMobTypeFromItem(filterItem);
                if (!mobType.isEmpty()) {
                    tag.putString("WardedMobType", mobType);
                }
            }
        }

        /**
         * Derive mob type from an item (e.g., rotten flesh → zombie)
         */
        private static String getMobTypeFromItem(ItemStack item)
        {
            if(item==null || item.isEmpty()) return "";

            Set<EntityType<?>> set = ModConfig.getEntityTypesWardedBy(item.getItem());
            String mobs = "";
            for(EntityType<?> entType : set) {
                mobs+= HBUtil.EntityUtil.entityTypeToCommonName(entType) + ", ";
            }
            //delete last char
            return mobs.isEmpty() ? "" : mobs.substring(0, mobs.length() - 2);
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