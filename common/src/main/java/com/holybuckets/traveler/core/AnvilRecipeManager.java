package com.holybuckets.traveler.core;

import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.AnvilUpdateEvent;
import com.holybuckets.traveler.config.ModConfig;
import com.holybuckets.traveler.enchantment.ModEnchantments;
import com.holybuckets.traveler.item.AnvilEnchantmentItem;
import com.holybuckets.traveler.item.ModItems;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashSet;
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

    // Whetstone recipes
    static AnvilUpdateEvent.EnchantDriven whetstoneEnchantDriven = new AnvilUpdateEvent.EnchantDriven(new HashSet<>(), ModItems.whetstone);
    static AnvilUpdateEvent.MaterialDriven whetstoneWeaponDriven = new AnvilUpdateEvent.MaterialDriven(new HashSet<>(), ModItems.whetstone);
    static AnvilUpdateEvent.EnchantDriven netheriteWhetstoneEnchantDriven = new AnvilUpdateEvent.EnchantDriven(new HashSet<>(), ModItems.netheriteWhetstone);
    static AnvilUpdateEvent.MaterialDriven netheriteWhetstoneWeaponDriven = new AnvilUpdateEvent.MaterialDriven(new HashSet<>(), ModItems.netheriteWhetstone);

    // Bracing recipes
    static AnvilUpdateEvent.EnchantDriven bracingEnchantDriven = new AnvilUpdateEvent.EnchantDriven(new HashSet<>(), ModItems.bracing);
    static AnvilUpdateEvent.MaterialDriven bracingToolDriven = new AnvilUpdateEvent.MaterialDriven(new HashSet<>(), ModItems.bracing);
    static AnvilUpdateEvent.EnchantDriven netheriteBracingEnchantDriven = new AnvilUpdateEvent.EnchantDriven(new HashSet<>(), ModItems.netheriteBracing);
    static AnvilUpdateEvent.MaterialDriven netheriteBracingToolDriven = new AnvilUpdateEvent.MaterialDriven(new HashSet<>(), ModItems.netheriteBracing);

    // Hourglass recipes
    static AnvilUpdateEvent.EnchantDriven hourglassEnchantDriven = new AnvilUpdateEvent.EnchantDriven(new HashSet<>(), ModItems.hourglass);
    static AnvilUpdateEvent.EnchantDriven diamondHourglassEnchantDriven = new AnvilUpdateEvent.EnchantDriven(new HashSet<>(), ModItems.diamondHourglass);

    // Repair recipes
    static AnvilUpdateEvent.MaterialDriven diamondShardRepair = new AnvilUpdateEvent.MaterialDriven(ModConfig.validDiamondRepairItems, ModItems.diamondShard);
    static AnvilUpdateEvent.MaterialDriven ironBloomRepair = new AnvilUpdateEvent.MaterialDriven(ModConfig.validIronRepairItems, ModItems.ironBloom);
    static AnvilUpdateEvent.MaterialDriven goldBloomRepair = new AnvilUpdateEvent.MaterialDriven(ModConfig.validGoldRepairItems, ModItems.goldBloom);
    static AnvilUpdateEvent.MaterialDriven netheriteBloomRepair = new AnvilUpdateEvent.MaterialDriven(ModConfig.validNetheriteRepairItems, ModItems.netheriteBloom);

    /**
     * Initialize anvil recipes and register event handlers
     */
    public static void init(EventRegistrar registrar) {
        // Whetstone recipes
        registrar.registerOnAnvilUpdate(whetstoneEnchantDriven, AnvilRecipeManager::onWhetstoneEnchantUpgrade);
        registrar.registerOnAnvilUpdate(whetstoneWeaponDriven, AnvilRecipeManager::onWhetstoneWeaponUpgrade);
        registrar.registerOnAnvilUpdate(netheriteWhetstoneEnchantDriven, AnvilRecipeManager::onWhetstoneEnchantUpgrade);
        registrar.registerOnAnvilUpdate(netheriteWhetstoneWeaponDriven, AnvilRecipeManager::onWhetstoneWeaponUpgrade);


        // Bracing recipes
        registrar.registerOnAnvilUpdate(bracingEnchantDriven, AnvilRecipeManager::onBracingEnchantUpgrade);
        registrar.registerOnAnvilUpdate(bracingToolDriven, AnvilRecipeManager::onBracingToolUpgrade);
        registrar.registerOnAnvilUpdate(netheriteBracingEnchantDriven, AnvilRecipeManager::onBracingEnchantUpgrade);
        registrar.registerOnAnvilUpdate(netheriteBracingToolDriven, AnvilRecipeManager::onBracingToolUpgrade);

        // Hourglass recipes
        registrar.registerOnAnvilUpdate(hourglassEnchantDriven, AnvilRecipeManager::onHourglassEnchantUpgrade);
        registrar.registerOnAnvilUpdate(diamondHourglassEnchantDriven, AnvilRecipeManager::onHourglassEnchantUpgrade);

        // Repair recipes
        registrar.registerOnAnvilUpdate(diamondShardRepair, AnvilRecipeManager::onDiamondShardRepair);
        registrar.registerOnAnvilUpdate(ironBloomRepair, AnvilRecipeManager::onIronBloomRepair);
        registrar.registerOnAnvilUpdate(goldBloomRepair, AnvilRecipeManager::onGoldBloomRepair);
        registrar.registerOnAnvilUpdate(netheriteBloomRepair, AnvilRecipeManager::onNetheriteBloomRepair);

        registrar.registerOnBeforeServerStarted(AnvilRecipeManager::completeAnvilRegistration, EventPriority.Lowest);
    }

    private static void completeAnvilRegistration(ServerStartingEvent event) {
        // Whetstone
        whetstoneEnchantDriven.setRightItem(ModItems.whetstone.getDefaultInstance());
        whetstoneEnchantDriven.setLeftEnchantments(Set.of(Enchantments.SHARPNESS));
        whetstoneWeaponDriven.setLeftMaterials(ModConfig.validWhetstoneItems);
        whetstoneWeaponDriven.setRightItem(ModItems.whetstone.getDefaultInstance());

        // Netherite Whetstone
        netheriteWhetstoneEnchantDriven.setRightItem(ModItems.netheriteWhetstone.getDefaultInstance());
        netheriteWhetstoneEnchantDriven.setLeftEnchantments(Set.of(Enchantments.SHARPNESS));
        netheriteWhetstoneWeaponDriven.setLeftMaterials(ModConfig.validWhetstoneItems);
        netheriteWhetstoneWeaponDriven.setRightItem(ModItems.netheriteWhetstone.getDefaultInstance());

        // Bracing
        bracingEnchantDriven.setRightItem(ModItems.bracing.getDefaultInstance());
        bracingEnchantDriven.setLeftEnchantments(Set.of(Enchantments.UNBREAKING));
        bracingToolDriven.setLeftMaterials(ModConfig.validBracingItems);
        bracingToolDriven.setRightItem(ModItems.bracing.getDefaultInstance());
        // Netherite Bracing
        netheriteBracingEnchantDriven.setRightItem(ModItems.netheriteBracing.getDefaultInstance());
        netheriteBracingEnchantDriven.setLeftEnchantments(Set.of(Enchantments.UNBREAKING));
        netheriteBracingToolDriven.setLeftMaterials(ModConfig.validBracingItems);
        netheriteBracingToolDriven.setRightItem(ModItems.netheriteBracing.getDefaultInstance());

        // Hourglass
        hourglassEnchantDriven.setRightItem(ModItems.hourglass.getDefaultInstance());
        hourglassEnchantDriven.setLeftEnchantments(Set.of(ModEnchantments.LASTING.get()));
        diamondHourglassEnchantDriven.setRightItem(ModItems.diamondHourglass.getDefaultInstance());
        diamondHourglassEnchantDriven.setLeftEnchantments(Set.of(ModEnchantments.LASTING.get()));

        // Repair items
        diamondShardRepair.setLeftMaterials(ModConfig.validDiamondRepairItems);
        diamondShardRepair.setRightItem(ModItems.diamondShard.getDefaultInstance());
        ironBloomRepair.setLeftMaterials(ModConfig.validIronRepairItems);
        ironBloomRepair.setRightItem(ModItems.ironBloom.getDefaultInstance());
        goldBloomRepair.setLeftMaterials(ModConfig.validGoldRepairItems);
        goldBloomRepair.setRightItem(ModItems.goldBloom.getDefaultInstance());
        netheriteBloomRepair.setLeftMaterials(ModConfig.validNetheriteRepairItems);
        netheriteBloomRepair.setRightItem(ModItems.netheriteBloom.getDefaultInstance());
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
        if (currentSharpness >= 10) return; // Already at max level
        if (currentSharpness >= 5 && anvilEnchantmentItem.getTier() < 2) return;

        int sharpMax = 5 * anvilEnchantmentItem.getTier();
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
        enchantments.put(Enchantments.SHARPNESS, aei.getTier() + currentSharpness);
        EnchantmentHelper.setEnchantments(enchantments, result);

        // Set result
        event.setResultItem(result);
        event.setCost(1); // Cheap for adding first level
    }

    /**
     * Bracing adds +1 Unbreaking to items that already have Unbreaking
     */
    private static void onBracingEnchantUpgrade(AnvilUpdateEvent event)
    {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!(rightItem.getItem() instanceof AnvilEnchantmentItem anvilEnchantmentItem))
            return;

        // Check if item has Unbreaking
        int currentUnbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, leftItem);
        if (currentUnbreaking <= 0) return; // let the tool recipe handle it
        if (currentUnbreaking >= 10) return; // Already at max level
        if (currentUnbreaking >= 5 && anvilEnchantmentItem.getTier() < 2) return;

        int unbreakingMax = 5 * anvilEnchantmentItem.getTier();
        int newUnbreaking = Math.min(currentUnbreaking + anvilEnchantmentItem.getTier(), unbreakingMax);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(Enchantments.UNBREAKING, newUnbreaking);
        EnchantmentHelper.setEnchantments(enchantments, result);

        event.setResultItem(result);
        event.setCost(1);
    }

    /**
     * Bracing adds Unbreaking I to any tool or armor
     */
    private static void onBracingToolUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches

        if (!ModConfig.validBracingItems.contains(leftItem.getItem()) || !(rightItem.getItem() instanceof AnvilEnchantmentItem aei))
            return;

        // Check if already has Unbreaking (let the enchant-driven recipe handle it)
        int currentUnbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, leftItem);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(Enchantments.UNBREAKING, aei.getTier() + currentUnbreaking);
        EnchantmentHelper.setEnchantments(enchantments, result);

        // Set result
        event.setResultItem(result);
        event.setCost(1);
    }


    /**
     * Hourglass adds +1 Lasting to items that already have Lasting
     */
    private static void onHourglassEnchantUpgrade(AnvilUpdateEvent event)
    {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!(rightItem.getItem() instanceof AnvilEnchantmentItem anvilEnchantmentItem))
            return;

        // Check if item has Lasting
        int currentLasting = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), leftItem);
        if (currentLasting <= 0) return; // let the tool recipe handle it
        if (currentLasting >= 10) return; // Already at max level

        int lastingMax = 5 * anvilEnchantmentItem.getTier();
        int newLasting = Math.min(currentLasting + anvilEnchantmentItem.getTier(), lastingMax);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(ModEnchantments.LASTING.get(), newLasting);
        EnchantmentHelper.setEnchantments(enchantments, result);

        ItemImplementation.getInstance().removeLastingExpiration(result);
        event.setResultItem(result);
        event.setCost(1);
    }


    /**
     * Diamond Shard repairs diamond tools (same amount as using a diamond)
     */
    private static void onDiamondShardRepair(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!ModConfig.validDiamondRepairItems.contains(leftItem.getItem()) || rightItem.getItem() != ModItems.diamondShard) {
            return;
        }
        if (!leftItem.isDamaged()) return; // Not damaged, no need to repair

        int repairAmount = leftItem.getMaxDamage() / 4;
        int currentDamage = leftItem.getDamageValue();
        int newDamage = Math.max(0, currentDamage - repairAmount);

        ItemStack result = leftItem.copy();
        result.setDamageValue(newDamage);
        event.setResultItem(result);
        event.setCost(1); // Moderate cost for repair
    }

    /**
     * Iron Bloom repairs iron tools (same amount as using an iron ingot)
     */
    private static void onIronBloomRepair(AnvilUpdateEvent event)
    {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!ModConfig.validDiamondRepairItems.contains(leftItem.getItem()) || rightItem.getItem() != ModItems.ironBloom)
            return;

        if (!leftItem.isDamaged()) return;

        int repairAmount = leftItem.getMaxDamage() / 4;
        int currentDamage = leftItem.getDamageValue();
        int newDamage = Math.max(0, currentDamage - repairAmount);

        ItemStack result = leftItem.copy();
        result.setDamageValue(newDamage);

        event.setResultItem(result);
        event.setCost(1); // Moderate cost for repair
    }

    /**
     * Gold Bloom repairs gold tools (same amount as using a gold ingot)
     */
    private static void onGoldBloomRepair(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        if (!ModConfig.validGoldRepairItems.contains(leftItem.getItem()) || rightItem.getItem() != ModItems.goldBloom)
            return;

        if (!leftItem.isDamaged()) return;

        int repairAmount = leftItem.getMaxDamage() / 4;
        int currentDamage = leftItem.getDamageValue();
        int newDamage = Math.max(0, currentDamage - repairAmount);

        ItemStack result = leftItem.copy();
        result.setDamageValue(newDamage);
        event.setResultItem(result);
        event.setCost(1); // Moderate cost for repair
    }

    /**
     * Netherite Bloom repairs netherite tools (same amount as using a netherite ingot)
     */
    private static void onNetheriteBloomRepair(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!ModConfig.validNetheriteRepairItems.contains(leftItem.getItem()) || rightItem.getItem() != ModItems.netheriteBloom) {
            return;
        }
        if (!leftItem.isDamaged()) return;

        // Calculate repair amount (1 netherite ingot = 25% of max durability)
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
