package com.holybuckets.traveler.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

/**
 * Lasting - A curse that prevents items from being repaired
 * Items with this curse cannot be repaired in anvils or mending
 */
public class LastingEnchantment extends Enchantment {

    public LastingEnchantment() {
        super(
            Rarity.VERY_RARE,           // Rarity
            EnchantmentCategory.BREAKABLE, // Can apply to any breakable item
            EquipmentSlot.values()      // Can be in any slot
        );
    }

    @Override
    public int getMinCost(int level) {
        return 25; // High minimum cost
    }

    @Override
    public int getMaxCost(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1; // Only one level
    }

    @Override
    public boolean isTreasureOnly() {
        return true; // Only from loot/trading, not enchanting table
    }

    @Override
    public boolean isCurse() {
        return true; // THIS MAKES IT A CURSE
    }

    @Override
    public boolean isTradeable() {
        return false; // Can't get from villagers
    }

    @Override
    public boolean isDiscoverable() {
        return false; // Won't appear in enchanting table
    }
}