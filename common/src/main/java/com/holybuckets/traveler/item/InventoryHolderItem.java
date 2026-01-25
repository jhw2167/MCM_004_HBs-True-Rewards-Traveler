package com.holybuckets.traveler.item;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for items that have internal inventory storage using NBT.
 * Provides methods for storing and retrieving items within the item's NBT data.
 *
 * Examples: Mob Ward, Empty Totem
 */
public abstract class InventoryHolderItem extends Item {

    private final String itemId;
    private final int inventorySize;

    public InventoryHolderItem(String itemId, int inventorySize) {
        super(Balm.getItems().itemProperties().stacksTo(1)); // Non-stackable due to NBT storage
        this.itemId = itemId;
        this.inventorySize = inventorySize;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.translatable("item.hbs_traveler_rewards." + itemId + ".desc"));

        // Display stored item information
        ItemStack storedItem = getStoredItem(stack, 0);
        if (!storedItem.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.hbs_traveler_rewards.contains", storedItem.getHoverName()));
        } else {
            tooltipComponents.add(Component.translatable("item.hbs_traveler_rewards.empty"));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        // Show enchanted glint if item contains something
        return !getStoredItem(stack, 0).isEmpty();
    }

    /**
     * Gets the stored item at the specified slot index
     */
    public ItemStack getStoredItem(ItemStack holderStack, int slot) {
        if (slot < 0 || slot >= inventorySize) {
            return ItemStack.EMPTY;
        }

        CompoundTag tag = holderStack.getOrCreateTag();
        if (tag.contains("StoredItem" + slot)) {
            return ItemStack.of(tag.getCompound("StoredItem" + slot));
        }
        return ItemStack.EMPTY;
    }

    /**
     * Sets the stored item at the specified slot index
     */
    public void setStoredItem(ItemStack holderStack, int slot, ItemStack itemToStore) {
        if (slot < 0 || slot >= inventorySize) {
            return;
        }

        CompoundTag tag = holderStack.getOrCreateTag();
        if (itemToStore.isEmpty()) {
            tag.remove("StoredItem" + slot);
        } else {
            CompoundTag itemTag = new CompoundTag();
            itemToStore.save(itemTag);
            tag.put("StoredItem" + slot, itemTag);
        }
    }

    /**
     * Gets all stored items as a container
     */
    public SimpleContainer getInventory(ItemStack holderStack) {
        SimpleContainer container = new SimpleContainer(inventorySize);
        for (int i = 0; i < inventorySize; i++) {
            container.setItem(i, getStoredItem(holderStack, i));
        }
        return container;
    }

    /**
     * Saves a container's contents to the holder item
     */
    public void setInventory(ItemStack holderStack, SimpleContainer container) {
        for (int i = 0; i < Math.min(inventorySize, container.getContainerSize()); i++) {
            setStoredItem(holderStack, i, container.getItem(i));
        }
    }

    /**
     * Checks if the holder has any stored items
     */
    public boolean hasStoredItems(ItemStack holderStack) {
        for (int i = 0; i < inventorySize; i++) {
            if (!getStoredItem(holderStack, i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all stored items
     */
    public void clearInventory(ItemStack holderStack) {
        for (int i = 0; i < inventorySize; i++) {
            setStoredItem(holderStack, i, ItemStack.EMPTY);
        }
    }

    public int getInventorySize() {
        return inventorySize;
    }

    public String getItemId() {
        return itemId;
    }
}