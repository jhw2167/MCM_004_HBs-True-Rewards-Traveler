package com.holybuckets.traveler.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Mob Ward - Place an item in the slot; any mob that drops that item is warded away
 * Art Inspiration: Same style as vanilla Minecraft totem
 * 
 * Note: The actual mob warding logic will be handled by an event listener that checks
 * if players have Mob Wards with items that match the mob's drops
 */
public class MobWardItem extends InventoryHolderItem {
    
    public MobWardItem() {
        super("mob_ward", 1); // Single slot for the item to ward against
    }
    
    /**
     * Gets the item that defines which mobs to ward away
     */
    public ItemStack getWardedItem(ItemStack wardStack) {
        return getStoredItem(wardStack, 0);
    }
    
    /**
     * Sets the item that defines which mobs to ward away
     */
    public void setWardedItem(ItemStack wardStack, ItemStack itemToWard) {
        setStoredItem(wardStack, 0, itemToWard);
    }
    
    /**
     * Checks if this ward should repel a specific item drop
     */
    public boolean wardsAgainstItem(ItemStack wardStack, Item droppedItem) {
        ItemStack wardedItem = getWardedItem(wardStack);
        return !wardedItem.isEmpty() && wardedItem.getItem() == droppedItem;
    }
}
