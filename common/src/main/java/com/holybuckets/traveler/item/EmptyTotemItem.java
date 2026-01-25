package com.holybuckets.traveler.item;

import net.minecraft.world.item.ItemStack;

/**
 * Empty Totem - A container that can be filled with items like Ender Eyes or Biome Essence
 * Can wrap an Ender Eye so it doesn't break, or wrap items to halt their effects
 * Art Inspiration: Same style as vanilla Minecraft totem of undying, but design implies something can be placed inside
 * 
 * Note: The actual preservation/halting logic will be handled by event listeners
 */
public class EmptyTotemItem extends InventoryHolderItem {
    
    public EmptyTotemItem() {
        super("empty_totem", 1); // Single slot for the wrapped item
    }
    
    /**
     * Gets the item stored/wrapped inside the totem
     */
    public ItemStack getWrappedItem(ItemStack totemStack) {
        return getStoredItem(totemStack, 0);
    }
    
    /**
     * Sets the item to be stored/wrapped inside the totem
     */
    public void setWrappedItem(ItemStack totemStack, ItemStack itemToWrap) {
        setStoredItem(totemStack, 0, itemToWrap);
    }
    
    /**
     * Checks if the totem currently has an item wrapped inside
     */
    public boolean hasWrappedItem(ItemStack totemStack) {
        return !getWrappedItem(totemStack).isEmpty();
    }
    
    /**
     * Removes and returns the wrapped item from the totem
     */
    public ItemStack extractWrappedItem(ItemStack totemStack) {
        ItemStack wrapped = getWrappedItem(totemStack);
        if (!wrapped.isEmpty()) {
            setWrappedItem(totemStack, ItemStack.EMPTY);
        }
        return wrapped;
    }
}
