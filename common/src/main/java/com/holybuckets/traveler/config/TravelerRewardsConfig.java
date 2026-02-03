package com.holybuckets.traveler.config;

import com.holybuckets.traveler.Constants;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.balm.api.config.reflection.NestedType;

import java.util.Set;


@Config(Constants.MOD_ID)
public class TravelerRewardsConfig {

    public static class SimpleRewardsConfig {
        @NestedType(String.class)
        @Comment("Blocks that can be duplicated by the fabrication ritual")
        public Set<String> fabricationRitualBlocksWhitelist = Set.of(
            /*Surface cave blocks*/ "minecraft:dirt", "minecraft:grass_block", "minecraft:cobblestone", "minecraft:stone", "minecraft:gravel",
            /*Dirts*/ "minecraft:coarse_dirt", "minecraft:podzol", "minecraft:mud", "minecraft:sand", "minecraft:red_sand", "minecraft:sand_stone", "minecraft:soul_sand", "minecraft:mycelium",
            /*Planks*/ "minecraft:oak_planks", "minecraft:spruce_planks", "minecraft:birch_planks", "minecraft:jungle_planks", "minecraft:acacia_planks", "minecraft:dark_oak_planks",
            /*Logs*/ "minecraft:oak_log", "minecraft:spruce_log", "minecraft:birch_log", "minecraft:jungle_log", "minecraft:acacia_log", "minecraft:dark_oak_log",
            /*other stone*/ "minecraft:andesite", "minecraft:diorite", "minecraft:granite", "minecraft:deepslate", "minecraft:tuff",
            /*Fired bricks*/ "minecraft:bricks", "minecraft:stone_bricks", "minecraft:nether_bricks",
            /*basalt, blackstone, endstone*/ "minecraft:basalt", "minecraft:blackstone", "minecraft:end_stone", "minecraft:prismarine");

    }

    public static class AnvilRewardsConfig {
        @NestedType(String.class)
        @Comment("Weapons compatible with whetstone in an anvil")
        public Set<String> whetstoneWeapons = Set.of(
            "minecraft:wooden_sword",
            "minecraft:stone_sword",
            "minecraft:iron_sword",
            "minecraft:golden_sword",
            "minecraft:diamond_sword",
            "minecraft:netherite_sword",
            "minecraft:wooden_axe",
            "minecraft:stone_axe",
            "minecraft:iron_axe",
            "minecraft:golden_axe",
            "minecraft:diamond_axe",
            "minecraft:netherite_axe",
            "minecraft:trident"
        );

        @NestedType(String.class)
        @Comment("Equipment compatible with bracing in an anvil (armor, shields, elytra). Automatically includes all weapons and armor")
        public Set<String> bracingEquipment = Set.of(
            "minecraft:turtle_helmet",
            "minecraft:elytra",
            "minecraft:shield"
        );

        @NestedType(String.class)
        @Comment("Tools and armor repairable with diamonds in an anvil")
        public Set<String> diamondRepairEquip = Set.of(
            "minecraft:diamond_sword",
            "minecraft:diamond_pickaxe",
            "minecraft:diamond_axe",
            "minecraft:diamond_shovel",
            "minecraft:diamond_hoe",
            "minecraft:diamond_helmet",
            "minecraft:diamond_chestplate",
            "minecraft:diamond_leggings",
            "minecraft:diamond_boots"
        );

        @NestedType(String.class)
        @Comment("Tools and armor repairable with iron ingots in an anvil (includes chainmail)")
        public Set<String> ironRepairEquip = Set.of(
            "minecraft:iron_sword",
            "minecraft:iron_pickaxe",
            "minecraft:iron_axe",
            "minecraft:iron_shovel",
            "minecraft:iron_hoe",
            "minecraft:iron_helmet",
            "minecraft:iron_chestplate",
            "minecraft:iron_leggings",
            "minecraft:iron_boots",
            "minecraft:chainmail_helmet",
            "minecraft:chainmail_chestplate",
            "minecraft:chainmail_leggings",
            "minecraft:chainmail_boots",
            "minecraft:shears",
            "minecraft:flint_and_steel"
        );

        @NestedType(String.class)
        @Comment("Tools and armor repairable with gold ingots in an anvil")
        public Set<String> goldRepairEquip = Set.of(
            "minecraft:golden_sword",
            "minecraft:golden_pickaxe",
            "minecraft:golden_axe",
            "minecraft:golden_shovel",
            "minecraft:golden_hoe",
            "minecraft:golden_helmet",
            "minecraft:golden_chestplate",
            "minecraft:golden_leggings",
            "minecraft:golden_boots"
        );

        @NestedType(String.class)
        @Comment("Tools and armor repairable with netherite ingots in an anvil")
        public Set<String> netheriteRepairEquip = Set.of(
            "minecraft:netherite_sword",
            "minecraft:netherite_pickaxe",
            "minecraft:netherite_axe",
            "minecraft:netherite_shovel",
            "minecraft:netherite_hoe",
            "minecraft:netherite_helmet",
            "minecraft:netherite_chestplate",
            "minecraft:netherite_leggings",
            "minecraft:netherite_boots"
        );
    }

    public AnvilRewardsConfig anvilRewards = new AnvilRewardsConfig();
    public SimpleRewardsConfig simpleRewards = new SimpleRewardsConfig();

}
