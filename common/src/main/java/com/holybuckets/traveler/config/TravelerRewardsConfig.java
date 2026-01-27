package com.holybuckets.traveler.config;

import com.holybuckets.traveler.Constants;
import net.blay09.mods.balm.api.config.reflection.Comment;
import net.blay09.mods.balm.api.config.reflection.Config;
import net.blay09.mods.balm.api.config.reflection.NestedType;

import java.util.Set;


@Config(Constants.MOD_ID)
public class TravelerRewardsConfig {

    @Comment("devMode==true disables portal spawns so the player can build and save new challenges")
    public boolean devMode = false;
    @Comment("Blocks that can be duplicated by the fabrication ritual")
    @NestedType(String.class)
    public Set<String> fabricationRitualBlocksWhitelist = Set.of(
    /*Surface cave blocks*/ "minecraft:dirt", "minecraft:grass_block", "minecraft:cobblestone", "minecraft:stone", "minecraft:gravel",
    /*Dirts*/ "minecraft:coarse_dirt", "minecraft:podzol", "minecraft:mud", "minecraft:sand", "minecraft:red_sand", "minecraft:sand_stone", "minecraft:soul_sand", "minecraft:mycelium",
     /*Planks*/ "minecraft:oak_planks", "minecraft:spruce_planks", "minecraft:birch_planks", "minecraft:jungle_planks", "minecraft:acacia_planks", "minecraft:dark_oak_planks",
     /*Logs*/ "minecraft:oak_log", "minecraft:spruce_log", "minecraft:birch_log", "minecraft:jungle_log", "minecraft:acacia_log", "minecraft:dark_oak_log",
     /*other stone*/ "minecraft:andesite", "minecraft:diorite", "minecraft:granite", "minecraft:deepslate", "minecraft:tuff",
     /*Fired bricks*/ "minecraft:bricks", "minecraft:stone_bricks", "minecraft:nether_bricks",
     /*basalt, blackstone, endstone*/ "minecraft:basalt", "minecraft:blackstone", "minecraft:end_stone", "minecraft:prismarine");



}