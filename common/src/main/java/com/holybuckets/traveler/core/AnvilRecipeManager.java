package com.holybuckets.traveler.core;

import com.holybuckets.foundation.LoggerBase;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.AnvilUpdateEvent;
import com.holybuckets.traveler.TravelerRewardsMain;
import com.holybuckets.traveler.config.ModConfig;
import com.holybuckets.traveler.item.AnvilEnchantmentItem;
import com.holybuckets.traveler.item.ModItems;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Map;
import java.util.Set;

/**
 * Manages anvil recipes for Traveler Rewards items
 */
public class AnvilRecipeManager {

    private static final String LOG_ID = "005";

    // Weapon sets for recipes
    private static final Set<Item> WEAPONS = Set.of(
        Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
        Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
        Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE,
        Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE
    );

    // Diamond tools for repair recipe
    private static final Set<Item> DIAMOND_TOOLS = Set.of(
        Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE,
        Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE
    );

    static AnvilUpdateEvent.EnchantDriven  whetstoneEnchantDriven = new AnvilUpdateEvent.EnchantDriven( Set.of(Enchantments.SHARPNESS), ModItems.whetstone);
    static AnvilUpdateEvent.MaterialDriven whetstoneWeaponDriven = new AnvilUpdateEvent.MaterialDriven( WEAPONS, ModItems.whetstone);
    static AnvilUpdateEvent.MaterialDriven diamondShardRepair = new AnvilUpdateEvent.MaterialDriven(DIAMOND_TOOLS, ModItems.diamondShard);


    /**
     * Initialize anvil recipes and register event handlers
     */
    public static void init(EventRegistrar registrar) {

        registrar.registerOnAnvilUpdate(whetstoneEnchantDriven, AnvilRecipeManager::onWhetstoneEnchantUpgrade);
        registrar.registerOnAnvilUpdate(whetstoneWeaponDriven, AnvilRecipeManager::onWhetstoneWeaponUpgrade);
        registrar.registerOnAnvilUpdate(diamondShardRepair, AnvilRecipeManager::onDiamondShardRepair);

        registrar.registerOnBeforeServerStarted( AnvilRecipeManager::completeAnvilRegistration );
        //LoggerBase.logInfo(null, LOG_ID, "Anvil recipes registered successfully");
    }

    private static void completeAnvilRegistration(ServerStartingEvent event) {
        whetstoneEnchantDriven.setRightItem(ModItems.whetstone.getDefaultInstance());

        whetstoneWeaponDriven.setLeftMaterials( ModConfig.validWhetstoneItems );
        whetstoneWeaponDriven.setRightItem(ModItems.whetstone.getDefaultInstance());

        diamondShardRepair.setLeftMaterials( ModConfig.validDiamondShardItems );
        diamondShardRepair.setRightItem(ModItems.diamondShard.getDefaultInstance());
    }

    /**
     * Whetstone adds +1 Sharpness to items that already have Sharpness
     */
    private static void onWhetstoneEnchantUpgrade(AnvilUpdateEvent event)
    {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!(rightItem.getItem() instanceof AnvilEnchantmentItem anvilEnchantmentItem))
            return;

        // Check if item has Sharpness
        int currentSharpness = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, leftItem);
        if (currentSharpness <= 0) return; // let the weapon recipe handle it
        if(currentSharpness >= 10) return; // Already at max level
        if(currentSharpness >= 5 && anvilEnchantmentItem.getTier()<2) return;

        int sharpMax = 5*anvilEnchantmentItem.getTier();
        int newSharpness = Math.min(currentSharpness + anvilEnchantmentItem.getTier(), sharpMax);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(Enchantments.SHARPNESS, newSharpness);
        EnchantmentHelper.setEnchantments(enchantments, result);

        event.setResultItem(result);
        event.setCost(1);
    }

    /**
     * Whetstone adds Sharpness I to any weapon (sword or axe)
     */
    private static void onWhetstoneWeaponUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!WEAPONS.contains(leftItem.getItem()) || !(rightItem.getItem() instanceof AnvilEnchantmentItem aei))
            return;

        // Check if already has Sharpness (let the enchant-driven recipe handle it)
        int currentSharpness = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SHARPNESS, leftItem);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(Enchantments.SHARPNESS, aei.getTier()+currentSharpness );
        EnchantmentHelper.setEnchantments(enchantments, result);

        // Set result
        event.setResultItem(result);
        event.setCost(1); // Cheap for adding first level
    }

    /**
     * Diamond Shard repairs diamond tools (same amount as using a diamond)
     */
    private static void onDiamondShardRepair(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!DIAMOND_TOOLS.contains(leftItem.getItem()) || rightItem.getItem() != ModItems.diamondShard) {
            return;
        }

        // Check if item is damaged
        if (!leftItem.isDamaged()) {
            return; // Not damaged, no need to repair
        }

        // Calculate repair amount (1 diamond = 25% of max durability)
        int repairAmount = leftItem.getMaxDamage() / 4;
        int currentDamage = leftItem.getDamageValue();
        int newDamage = Math.max(0, currentDamage - repairAmount);

        // Create repaired item
        ItemStack result = leftItem.copy();
        result.setDamageValue(newDamage);

        // Set result
        event.setResultItem(result);
        event.setCost(1); // Moderate cost for repair
    }
}