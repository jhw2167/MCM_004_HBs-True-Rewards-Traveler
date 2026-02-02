package com.holybuckets.traveler.core;

import com.holybuckets.foundation.LoggerBase;
import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.event.custom.AnvilUpdateEvent;
import com.holybuckets.traveler.TravelerRewardsMain;
import com.holybuckets.traveler.config.ModConfig;
import com.holybuckets.traveler.enchantment.ModEnchantments;
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

    // Tools and armor for Unbreaking
    private static final Set<Item> TOOLS_AND_ARMOR = Set.of(
        Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD,
        Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
        Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE,
        Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE,
        Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Items.IRON_PICKAXE,
        Items.GOLDEN_PICKAXE, Items.DIAMOND_PICKAXE, Items.NETHERITE_PICKAXE,
        Items.WOODEN_SHOVEL, Items.STONE_SHOVEL, Items.IRON_SHOVEL,
        Items.GOLDEN_SHOVEL, Items.DIAMOND_SHOVEL, Items.NETHERITE_SHOVEL,
        Items.WOODEN_HOE, Items.STONE_HOE, Items.IRON_HOE,
        Items.GOLDEN_HOE, Items.DIAMOND_HOE, Items.NETHERITE_HOE,
        Items.LEATHER_HELMET, Items.LEATHER_CHESTPLATE, Items.LEATHER_LEGGINGS, Items.LEATHER_BOOTS,
        Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS,
        Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS,
        Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS,
        Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS,
        Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS
    );

    // Diamond tools for repair recipe
    private static final Set<Item> DIAMOND_TOOLS = Set.of(
        Items.DIAMOND_SWORD, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE,
        Items.DIAMOND_SHOVEL, Items.DIAMOND_HOE
    );

    // Iron tools for repair recipe
    private static final Set<Item> IRON_TOOLS = Set.of(
        Items.IRON_SWORD, Items.IRON_PICKAXE, Items.IRON_AXE,
        Items.IRON_SHOVEL, Items.IRON_HOE
    );

    // Gold tools for repair recipe
    private static final Set<Item> GOLD_TOOLS = Set.of(
        Items.GOLDEN_SWORD, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE,
        Items.GOLDEN_SHOVEL, Items.GOLDEN_HOE
    );

    // Netherite tools for repair recipe
    private static final Set<Item> NETHERITE_TOOLS = Set.of(
        Items.NETHERITE_SWORD, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE,
        Items.NETHERITE_SHOVEL, Items.NETHERITE_HOE
    );

    // Whetstone recipes
    static AnvilUpdateEvent.EnchantDriven whetstoneEnchantDriven = new AnvilUpdateEvent.EnchantDriven(Set.of(Enchantments.SHARPNESS), ModItems.whetstone);
    static AnvilUpdateEvent.MaterialDriven whetstoneWeaponDriven = new AnvilUpdateEvent.MaterialDriven(WEAPONS, ModItems.whetstone);

    // Bracing recipes
    static AnvilUpdateEvent.EnchantDriven bracingEnchantDriven = new AnvilUpdateEvent.EnchantDriven(Set.of(Enchantments.UNBREAKING), ModItems.bracing);
    static AnvilUpdateEvent.MaterialDriven bracingToolDriven = new AnvilUpdateEvent.MaterialDriven(TOOLS_AND_ARMOR, ModItems.bracing);
    static AnvilUpdateEvent.EnchantDriven netheriteBracingEnchantDriven = new AnvilUpdateEvent.EnchantDriven(Set.of(Enchantments.UNBREAKING), ModItems.netheriteBracing);
    static AnvilUpdateEvent.MaterialDriven netheriteBracingToolDriven = new AnvilUpdateEvent.MaterialDriven(TOOLS_AND_ARMOR, ModItems.netheriteBracing);

    // Hourglass recipes
    static AnvilUpdateEvent.EnchantDriven hourglassEnchantDriven = new AnvilUpdateEvent.EnchantDriven(Set.of(ModEnchantments.LASTING.get()), ModItems.hourglass);
    static AnvilUpdateEvent.MaterialDriven hourglassToolDriven = new AnvilUpdateEvent.MaterialDriven(TOOLS_AND_ARMOR, ModItems.hourglass);
    static AnvilUpdateEvent.EnchantDriven diamondHourglassEnchantDriven = new AnvilUpdateEvent.EnchantDriven(Set.of(ModEnchantments.LASTING.get()), ModItems.diamondHourglass);
    static AnvilUpdateEvent.MaterialDriven diamondHourglassToolDriven = new AnvilUpdateEvent.MaterialDriven(TOOLS_AND_ARMOR, ModItems.diamondHourglass);

    // Repair recipes
    static AnvilUpdateEvent.MaterialDriven diamondShardRepair = new AnvilUpdateEvent.MaterialDriven(DIAMOND_TOOLS, ModItems.diamondShard);
    static AnvilUpdateEvent.MaterialDriven ironBloomRepair = new AnvilUpdateEvent.MaterialDriven(IRON_TOOLS, ModItems.ironBloom);
    static AnvilUpdateEvent.MaterialDriven goldBloomRepair = new AnvilUpdateEvent.MaterialDriven(GOLD_TOOLS, ModItems.goldBloom);
    static AnvilUpdateEvent.MaterialDriven netheriteBloomRepair = new AnvilUpdateEvent.MaterialDriven(NETHERITE_TOOLS, ModItems.netheriteBloom);

    /**
     * Initialize anvil recipes and register event handlers
     */
    public static void init(EventRegistrar registrar) {
        // Whetstone recipes
        registrar.registerOnAnvilUpdate(whetstoneEnchantDriven, AnvilRecipeManager::onWhetstoneEnchantUpgrade);
        registrar.registerOnAnvilUpdate(whetstoneWeaponDriven, AnvilRecipeManager::onWhetstoneWeaponUpgrade);

        // Bracing recipes
        registrar.registerOnAnvilUpdate(bracingEnchantDriven, AnvilRecipeManager::onBracingEnchantUpgrade);
        registrar.registerOnAnvilUpdate(bracingToolDriven, AnvilRecipeManager::onBracingToolUpgrade);
        registrar.registerOnAnvilUpdate(netheriteBracingEnchantDriven, AnvilRecipeManager::onNetheriteBracingEnchantUpgrade);
        registrar.registerOnAnvilUpdate(netheriteBracingToolDriven, AnvilRecipeManager::onNetheriteBracingToolUpgrade);

        // Hourglass recipes
        registrar.registerOnAnvilUpdate(hourglassEnchantDriven, AnvilRecipeManager::onHourglassEnchantUpgrade);
        registrar.registerOnAnvilUpdate(hourglassToolDriven, AnvilRecipeManager::onHourglassToolUpgrade);
        registrar.registerOnAnvilUpdate(diamondHourglassEnchantDriven, AnvilRecipeManager::onDiamondHourglassEnchantUpgrade);
        registrar.registerOnAnvilUpdate(diamondHourglassToolDriven, AnvilRecipeManager::onDiamondHourglassToolUpgrade);

        // Repair recipes
        registrar.registerOnAnvilUpdate(diamondShardRepair, AnvilRecipeManager::onDiamondShardRepair);
        registrar.registerOnAnvilUpdate(ironBloomRepair, AnvilRecipeManager::onIronBloomRepair);
        registrar.registerOnAnvilUpdate(goldBloomRepair, AnvilRecipeManager::onGoldBloomRepair);
        registrar.registerOnAnvilUpdate(netheriteBloomRepair, AnvilRecipeManager::onNetheriteBloomRepair);

        registrar.registerOnBeforeServerStarted(AnvilRecipeManager::completeAnvilRegistration);
    }

    private static void completeAnvilRegistration(ServerStartingEvent event) {
        // Whetstone
        whetstoneEnchantDriven.setRightItem(ModItems.whetstone.getDefaultInstance());
        whetstoneWeaponDriven.setLeftMaterials(ModConfig.validWhetstoneItems);
        whetstoneWeaponDriven.setRightItem(ModItems.whetstone.getDefaultInstance());

        // Bracing
        bracingEnchantDriven.setRightItem(ModItems.bracing.getDefaultInstance());
        bracingToolDriven.setLeftMaterials(ModConfig.validBracingItems);
        bracingToolDriven.setRightItem(ModItems.bracing.getDefaultInstance());
        netheriteBracingEnchantDriven.setRightItem(ModItems.netheriteBracing.getDefaultInstance());
        netheriteBracingToolDriven.setLeftMaterials(ModConfig.validBracingItems);
        netheriteBracingToolDriven.setRightItem(ModItems.netheriteBracing.getDefaultInstance());

        // Hourglass
        hourglassEnchantDriven.setRightItem(ModItems.hourglass.getDefaultInstance());
        hourglassToolDriven.setLeftMaterials(ModConfig.validHourglassItems);
        hourglassToolDriven.setRightItem(ModItems.hourglass.getDefaultInstance());
        diamondHourglassEnchantDriven.setRightItem(ModItems.diamondHourglass.getDefaultInstance());
        diamondHourglassToolDriven.setLeftMaterials(ModConfig.validHourglassItems);
        diamondHourglassToolDriven.setRightItem(ModItems.diamondHourglass.getDefaultInstance());

        // Repair items
        diamondShardRepair.setLeftMaterials(ModConfig.validDiamondShardItems);
        diamondShardRepair.setRightItem(ModItems.diamondShard.getDefaultInstance());
        ironBloomRepair.setLeftMaterials(ModConfig.validIronBloomItems);
        ironBloomRepair.setRightItem(ModItems.ironBloom.getDefaultInstance());
        goldBloomRepair.setLeftMaterials(ModConfig.validGoldBloomItems);
        goldBloomRepair.setRightItem(ModItems.goldBloom.getDefaultInstance());
        netheriteBloomRepair.setLeftMaterials(ModConfig.validNetheriteBloomItems);
        netheriteBloomRepair.setRightItem(ModItems.netheriteBloom.getDefaultInstance());
    }

    /**
     * Whetstone adds +1 Sharpness to items that already have Sharpness
     */
    private static void onWhetstoneEnchantUpgrade(AnvilUpdateEvent event) {
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
    private static void onBracingEnchantUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!(rightItem.getItem() instanceof AnvilEnchantmentItem anvilEnchantmentItem))
            return;

        // Check if item has Unbreaking
        int currentUnbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, leftItem);
        if (currentUnbreaking <= 0) return; // let the tool recipe handle it
        if (currentUnbreaking >= 6) return; // Already at max level
        if (currentUnbreaking >= 3 && anvilEnchantmentItem.getTier() < 2) return;

        int unbreakingMax = 3 * anvilEnchantmentItem.getTier();
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
        if (!TOOLS_AND_ARMOR.contains(leftItem.getItem()) || !(rightItem.getItem() instanceof AnvilEnchantmentItem aei))
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
     * Netherite Bracing adds +2 Unbreaking to items that already have Unbreaking
     */
    private static void onNetheriteBracingEnchantUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!(rightItem.getItem() instanceof AnvilEnchantmentItem anvilEnchantmentItem))
            return;

        // Check if item has Unbreaking
        int currentUnbreaking = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.UNBREAKING, leftItem);
        if (currentUnbreaking <= 0) return; // let the tool recipe handle it
        if (currentUnbreaking >= 6) return; // Already at max level

        int newUnbreaking = Math.min(currentUnbreaking + anvilEnchantmentItem.getTier(), 6);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(Enchantments.UNBREAKING, newUnbreaking);
        EnchantmentHelper.setEnchantments(enchantments, result);

        event.setResultItem(result);
        event.setCost(1);
    }

    /**
     * Netherite Bracing adds Unbreaking II to any tool or armor
     */
    private static void onNetheriteBracingToolUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!TOOLS_AND_ARMOR.contains(leftItem.getItem()) || !(rightItem.getItem() instanceof AnvilEnchantmentItem aei))
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
    private static void onHourglassEnchantUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!(rightItem.getItem() instanceof AnvilEnchantmentItem anvilEnchantmentItem))
            return;

        // Check if item has Lasting
        int currentLasting = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), leftItem);
        if (currentLasting <= 0) return; // let the tool recipe handle it
        if (currentLasting >= 10) return; // Already at max level
        if (currentLasting >= 5 && anvilEnchantmentItem.getTier() < 2) return;

        int lastingMax = 5 * anvilEnchantmentItem.getTier();
        int newLasting = Math.min(currentLasting + anvilEnchantmentItem.getTier(), lastingMax);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(ModEnchantments.LASTING.get(), newLasting);
        EnchantmentHelper.setEnchantments(enchantments, result);

        event.setResultItem(result);
        event.setCost(1);
    }

    /**
     * Hourglass adds Lasting I to any tool or armor
     */
    private static void onHourglassToolUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!TOOLS_AND_ARMOR.contains(leftItem.getItem()) || !(rightItem.getItem() instanceof AnvilEnchantmentItem aei))
            return;

        // Check if already has Lasting (let the enchant-driven recipe handle it)
        int currentLasting = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), leftItem);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(ModEnchantments.LASTING.get(), aei.getTier() + currentLasting);
        EnchantmentHelper.setEnchantments(enchantments, result);

        // Set result
        event.setResultItem(result);
        event.setCost(1);
    }

    /**
     * Diamond Hourglass adds +2 Lasting to items that already have Lasting
     */
    private static void onDiamondHourglassEnchantUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!(rightItem.getItem() instanceof AnvilEnchantmentItem anvilEnchantmentItem))
            return;

        // Check if item has Lasting
        int currentLasting = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), leftItem);
        if (currentLasting <= 0) return; // let the tool recipe handle it
        if (currentLasting >= 10) return; // Already at max level

        int newLasting = Math.min(currentLasting + anvilEnchantmentItem.getTier(), 10);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(ModEnchantments.LASTING.get(), newLasting);
        EnchantmentHelper.setEnchantments(enchantments, result);

        event.setResultItem(result);
        event.setCost(1);
    }

    /**
     * Diamond Hourglass adds Lasting II to any tool or armor
     */
    private static void onDiamondHourglassToolUpgrade(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!TOOLS_AND_ARMOR.contains(leftItem.getItem()) || !(rightItem.getItem() instanceof AnvilEnchantmentItem aei))
            return;

        // Check if already has Lasting (let the enchant-driven recipe handle it)
        int currentLasting = EnchantmentHelper.getItemEnchantmentLevel(ModEnchantments.LASTING.get(), leftItem);
        ItemStack result = leftItem.copy();

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(result);
        enchantments.put(ModEnchantments.LASTING.get(), aei.getTier() + currentLasting);
        EnchantmentHelper.setEnchantments(enchantments, result);

        // Set result
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

    /**
     * Iron Bloom repairs iron tools (same amount as using an iron ingot)
     */
    private static void onIronBloomRepair(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!IRON_TOOLS.contains(leftItem.getItem()) || rightItem.getItem() != ModItems.ironBloom) {
            return;
        }

        // Check if item is damaged
        if (!leftItem.isDamaged()) {
            return; // Not damaged, no need to repair
        }

        // Calculate repair amount (1 iron ingot = 25% of max durability)
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

    /**
     * Gold Bloom repairs gold tools (same amount as using a gold ingot)
     */
    private static void onGoldBloomRepair(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!GOLD_TOOLS.contains(leftItem.getItem()) || rightItem.getItem() != ModItems.goldBloom) {
            return;
        }

        // Check if item is damaged
        if (!leftItem.isDamaged()) {
            return; // Not damaged, no need to repair
        }

        // Calculate repair amount (1 gold ingot = 25% of max durability)
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

    /**
     * Netherite Bloom repairs netherite tools (same amount as using a netherite ingot)
     */
    private static void onNetheriteBloomRepair(AnvilUpdateEvent event) {
        ItemStack leftItem = event.getLeftItem();
        ItemStack rightItem = event.getRightItem();

        // Check if recipe matches
        if (!NETHERITE_TOOLS.contains(leftItem.getItem()) || rightItem.getItem() != ModItems.netheriteBloom) {
            return;
        }

        // Check if item is damaged
        if (!leftItem.isDamaged()) {
            return; // Not damaged, no need to repair
        }

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
