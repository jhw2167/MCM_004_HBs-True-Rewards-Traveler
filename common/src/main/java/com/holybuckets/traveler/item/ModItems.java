package com.holybuckets.traveler.item;

import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.item.SimpleRewardItem;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.blay09.mods.balm.api.item.BalmItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import static com.holybuckets.traveler.Constants.MOD_ID;

/**
 * Registration class for all HB's Traveler Rewards items
 */
public class ModItems {


    // Creative tab
    public static DeferredObject<CreativeModeTab> creativeModeTab;

    // Simple Reward Items (basic items with no special behavior)
    public static SimpleRewardItem saviorOrb;

    public static SimpleRewardItem ironBloom;
    public static SimpleRewardItem goldBloom;
    public static SimpleRewardItem diamondShard;
    public static SimpleRewardItem netheriteBloom;

    // Interactive Reward Items (right-click interactions)
    public static SimpleRewardItem quarterHeart;
    public static SimpleRewardItem halfHeart;
    public static PureHeartItem pureHeart;
    public static SoulboundRitualTabletItem soulboundRitualTablet;
    public static FabricationRitualTabletItem fabricationRitualTablet;
    public static WarriorRitualTabletItem warriorRitualTablet;
    public static PotionPotItem potionPot;
    public static EscapeCharmItem escapeCharm;

    // Anvil Enchantment Items (anvil-only enhancements)
    // Tier 1 (Basic)
    public static AnvilEnchantmentItem whetstone;
    public static AnvilEnchantmentItem bracing;
    public static AnvilEnchantmentItem hourglass;

    // Tier 2 (Upgraded)
    public static AnvilEnchantmentItem netheriteWhetstone;
    public static AnvilEnchantmentItem netheriteBracing;
    public static AnvilEnchantmentItem diamondHourglass;

    // Inventory Holder Items (items with internal storage)
    public static MobWardItem mobWard;
    public static EmptyTotemItem emptyTotem;

    // Block Items (placeable blocks) - TODO: Register these when blocks are created
    // public static BlockItem crackedBeacon;
    // public static BlockItem ironBeaconFacade;

    /**
     * Initialize and register all items
     */
    public static void initialize(BalmItems items) {
        // Register creative tab
        creativeModeTab = items.registerCreativeModeTab(
            id(MOD_ID),
            () -> new ItemStack(hourglass)
        );

        // Register Simple Reward Items
        items.registerItem(() -> saviorOrb = new SimpleRewardItem("savior_orb"), id("savior_orb"));

        //
        items.registerItem(() -> ironBloom = new SimpleRewardItem("iron_bloom"), id("iron_bloom"));
        items.registerItem(() -> goldBloom = new SimpleRewardItem("gold_bloom"), id("gold_bloom"));
        items.registerItem(() -> netheriteBloom = new SimpleRewardItem("netherite_bloom"), id("netherite_bloom"));
        items.registerItem(() -> diamondShard = new SimpleRewardItem("diamond_shard"), id("diamond_shard"));


        // Register Interactive Reward Items
        //add SimpleRewardItem: quarter_heart, and half_heart
        items.registerItem(() -> quarterHeart = new SimpleRewardItem("quarter_heart"), id("quarter_heart"));
        items.registerItem(() -> halfHeart = new SimpleRewardItem("half_heart"), id("half_heart"));
        items.registerItem(() -> pureHeart = new PureHeartItem(), id("pure_heart"));
        items.registerItem(() -> soulboundRitualTablet = new SoulboundRitualTabletItem(), id("soulbound_ritual_tablet"));
        items.registerItem(() -> fabricationRitualTablet = new FabricationRitualTabletItem(), id("fabrication_ritual_tablet"));
        items.registerItem(() -> warriorRitualTablet = new WarriorRitualTabletItem(), id("warrior_ritual_tablet"));
        items.registerItem(() -> potionPot = new PotionPotItem(), id("potion_pot"));
        items.registerItem(() -> escapeCharm = new EscapeCharmItem(), id("escape_charm"));

        // Register Anvil Enchantment Items - Tier 1
        items.registerItem(() -> whetstone = new AnvilEnchantmentItem(
            "whetstone",
            AnvilEnchantmentItem.EnchantmentType.SHARPNESS,
            1
        ), id("whetstone"));

        items.registerItem(() -> bracing = new AnvilEnchantmentItem(
            "bracing",
            AnvilEnchantmentItem.EnchantmentType.UNBREAKING,
            1
        ), id("bracing"));

        items.registerItem(() -> hourglass = new AnvilEnchantmentItem(
            "hourglass",
            AnvilEnchantmentItem.EnchantmentType.LASTING,
            1
        ), id("hourglass"));

        // Register Anvil Enchantment Items - Tier 2 (Upgraded)
        items.registerItem(() -> netheriteWhetstone = new AnvilEnchantmentItem(
            "netherite_whetstone",
            AnvilEnchantmentItem.EnchantmentType.SHARPNESS,
            2
        ), id("netherite_whetstone"));

        items.registerItem(() -> netheriteBracing = new AnvilEnchantmentItem(
            "netherite_bracing",
            AnvilEnchantmentItem.EnchantmentType.UNBREAKING,
            2
        ), id("netherite_bracing"));

        items.registerItem(() -> diamondHourglass = new AnvilEnchantmentItem(
            "diamond_hourglass",
            AnvilEnchantmentItem.EnchantmentType.LASTING,
            2
        ), id("diamond_hourglass"));

        // Register Inventory Holder Items
        items.registerItem(() -> mobWard = new MobWardItem(), id("mob_ward"));
        items.registerItem(() -> emptyTotem = new EmptyTotemItem(), id("empty_totem"));
    }

    public static void init(EventRegistrar reg) {
        reg.registerOnBeforeServerStarted(ModItems::onBeforeServerStarted);
    }

    private static void onBeforeServerStarted(ServerStartingEvent event) {
        ItemStack stack = com.holybuckets.foundation.item.ModItems.enchantedEssence.getDefaultInstance();
        creativeModeTab.get().getDisplayItems().add(stack);
    }

    /**
     * Creates a ResourceLocation with the mod's namespace
     */
    private static ResourceLocation id(String name) {
        return new ResourceLocation(MOD_ID, name);
    }
}