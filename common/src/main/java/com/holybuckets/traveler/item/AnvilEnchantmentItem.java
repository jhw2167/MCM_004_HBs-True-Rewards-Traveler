package com.holybuckets.traveler.item;

import net.blay09.mods.balm.api.Balm;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Base class for enchantment-like items that can only be applied in an anvil.
 * Similar to enchanted books but for custom enhancements.
 *
 * Examples: Whetstone, Bracing, Hourglass and their upgraded versions
 */
public class AnvilEnchantmentItem extends Item {

    private final String itemId;
    private final EnchantmentType enchantmentType;
    private final int tier; // 1 = basic (iron/regular), 2 = upgraded (netherite/diamond)

    public AnvilEnchantmentItem(String itemId, EnchantmentType enchantmentType, int tier) {
        super(Balm.getItems().itemProperties().stacksTo(16)); // Similar to enchanted books
        this.itemId = itemId;
        this.enchantmentType = enchantmentType;
        this.tier = tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
        tooltipComponents.add(Component.translatable("item.hbs_traveler_rewards." + itemId + ".desc"));

        // Add tier information
        if (tier > 1) {
            tooltipComponents.add(Component.translatable("item.hbs_traveler_rewards.tier", tier));
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false; // Cannot be enchanted in an enchanting table
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true; // Give it the enchanted glint effect
    }

    public EnchantmentType getEnchantmentType() {
        return enchantmentType;
    }

    public int getTier() {
        return tier;
    }

    public String getItemId() {
        return itemId;
    }

    /**
     * Enum defining the types of enhancements these items provide
     */
    public enum EnchantmentType {
        SHARPNESS("sharpness"),     // Whetstone - adds Sharpness
        UNBREAKING("unbreaking"),   // Bracing - adds Unbreaking
        LASTING("lasting");         // Hourglass - adds custom Lasting enchantment

        private final String name;

        EnchantmentType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}