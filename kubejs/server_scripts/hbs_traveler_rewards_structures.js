// priority: 0

// Visit the wiki for more info - https://kubejs.com/


LootJS.modifiers((event) => {

    
    // Village (blacksmith chests)
    event.addLootTableModifier("minecraft:chests/village/village_weaponsmith")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    //event.addLootTableModifier("minecraft:chests/village/village_weaponsmith").randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:bracing"));
    
    event.addLootTableModifier("minecraft:chests/village/village_armorer")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    //event.addLootTableModifier("minecraft:chests/village/village_armorer").randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:bracing"));
    
    event.addLootTableModifier("minecraft:chests/village/village_toolsmith")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    //event.addLootTableModifier("minecraft:chests/village/village_toolsmith").randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:bracing"));
    
    // Mineshaft
    event.addLootTableModifier("minecraft:chests/abandoned_mineshaft")
        .randomChance(0.05).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    event.addLootTableModifier("minecraft:chests/abandoned_mineshaft")
        .randomChance(0.05).addLoot(Item.of("hbs_traveler_rewards:bracing"));
    event.addLootTableModifier("minecraft:chests/abandoned_mineshaft")
        .randomChance(0.10).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/abandoned_mineshaft")
        .randomChance(0.10).addLoot(LootEntry.of("hbs_traveler_rewards:potion_pot").setCount([1, 3]));
    event.addLootTableModifier("minecraft:chests/abandoned_mineshaft")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:diamond_shard"));

    
    // Dungeon
    event.addLootTableModifier("minecraft:chests/simple_dungeon")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:quarter_heart"));
    event.addLootTableModifier("minecraft:chests/simple_dungeon")
        .randomChance(0.10).addLoot(LootEntry.of("hbs_traveler_rewards:potion_pot").setCount([1, 3]));
    event.addLootTableModifier("minecraft:chests/simple_dungeon")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:diamond_shard"));
    event.addLootTableModifier("minecraft:chests/simple_dungeon")
        .randomChance(0.075)
        .addLoot(LootEntry.of("hbs_traveler_rewards:iron_facade_block").setCount([1, 4]));
    event.addLootTableModifier("minecraft:chests/simple_dungeon")
        .randomChance(0.075)
        .addLoot(LootEntry.of("hbs_traveler_rewards:gold_facade_block").setCount([1, 4]));

    
    // Desert Pyramid
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:quarter_heart"));
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.01).addLoot(Item.of("hbs_traveler_rewards:half_heart"));
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.10).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:escape_charm"));
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.075).addLoot(LootEntry.of("hbs_traveler_rewards:potion_pot").setCount([1, 3]));
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.075)
        .addLoot(LootEntry.of("hbs_traveler_rewards:iron_facade_block").setCount([1, 4]));
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.075)
        .addLoot(LootEntry.of("hbs_traveler_rewards:gold_facade_block").setCount([1, 4]));
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.01)
        .addLoot(LootEntry.of("hbs_traveler_rewards:diamond_facade_block").setCount([1, 2]));
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.005)
        .addLoot(LootEntry.of("hbs_traveler_rewards:emerald_facade_block").setCount([1, 2]));


    // Jungle Pyramid
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:quarter_heart"));
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.01).addLoot(Item.of("hbs_traveler_rewards:half_heart"));
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.10).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:escape_charm"));
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.075).addLoot(LootEntry.of("hbs_traveler_rewards:potion_pot").setCount([1, 3]));
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.075)
        .addLoot(LootEntry.of("hbs_traveler_rewards:iron_facade_block").setCount([1, 4]));
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.075)
        .addLoot(LootEntry.of("hbs_traveler_rewards:gold_facade_block").setCount([1, 4]));
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.01)
        .addLoot(LootEntry.of("hbs_traveler_rewards:diamond_facade_block").setCount([1, 2]));
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.005)
        .addLoot(LootEntry.of("hbs_traveler_rewards:emerald_facade_block").setCount([1, 2]));


    // Ocean Monument / Buried Treasure
    event.addLootTableModifier("minecraft:chests/buried_treasure")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:quarter_heart"));
    event.addLootTableModifier("minecraft:chests/buried_treasure")
        .randomChance(0.10).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    
    
    // Stronghold
    event.addLootTableModifier("minecraft:chests/stronghold_library")
        .randomChance(0.20).addLoot(Item.of("hbs_traveler_rewards:weathered_beacon"));

    
    // Pillager Outpost (common chests - no facade blocks)
    event.addLootTableModifier("minecraft:chests/pillager_outpost")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    event.addLootTableModifier("minecraft:chests/pillager_outpost")
        .randomChance(0.075).addLoot(Item.of("hbs_traveler_rewards:empty_totem"));

    
    // Woodland Mansion (no facade blocks)
    event.addLootTableModifier("minecraft:chests/woodland_mansion")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    event.addLootTableModifier("minecraft:chests/woodland_mansion")
        .randomChance(0.075).addLoot(Item.of("hbs_traveler_rewards:empty_totem"));
    event.addLootTableModifier("minecraft:chests/woodland_mansion")
        .randomChance(0.05).addLoot(Item.of("hbs_traveler_rewards:weathered_beacon"));

    
    // Nether Fortress - common chests (bridges/crossroads): potion pots, escape charms, whetstones. No facade blocks.
    event.addLootTableModifier("minecraft:chests/nether_bridge")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    event.addLootTableModifier("minecraft:chests/nether_bridge")
        .randomChance(0.10).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/nether_bridge")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:escape_charm"));
    event.addLootTableModifier("minecraft:chests/nether_bridge")
        .randomChance(0.075).addLoot(LootEntry.of("hbs_traveler_rewards:potion_pot").setCount([1, 3]));
    event.addLootTableModifier("minecraft:chests/nether_bridge")
        .randomChance(0.125).addLoot(Item.of("hbs_traveler_rewards:diamond_shard"));

    
    // Bastion - common chests (bridge, hoglin stable, other): potion pots, escape charms, whetstones. No facade blocks.
    event.addLootTableModifier("minecraft:chests/bastion_other")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    event.addLootTableModifier("minecraft:chests/bastion_other")
        .randomChance(0.075).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/bastion_other")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:escape_charm"));
    event.addLootTableModifier("minecraft:chests/bastion_other")
        .randomChance(0.05).addLoot(LootEntry.of("hbs_traveler_rewards:potion_pot").setCount([1, 3]));

    event.addLootTableModifier("minecraft:chests/bastion_bridge")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    event.addLootTableModifier("minecraft:chests/bastion_bridge")
        .randomChance(0.075).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/bastion_bridge")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:escape_charm"));
    event.addLootTableModifier("minecraft:chests/bastion_bridge")
        .randomChance(0.05).addLoot(LootEntry.of("hbs_traveler_rewards:potion_pot").setCount([1, 3]));

    event.addLootTableModifier("minecraft:chests/bastion_hoglin_stable")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:whetstone"));
    event.addLootTableModifier("minecraft:chests/bastion_hoglin_stable")
        .randomChance(0.075).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/bastion_hoglin_stable")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:escape_charm"));
    event.addLootTableModifier("minecraft:chests/bastion_hoglin_stable")
        .randomChance(0.05).addLoot(LootEntry.of("hbs_traveler_rewards:potion_pot").setCount([1, 3]));

    // Bastion - treasure room: warrior ritual tablets, half hearts. No facade blocks.
    event.addLootTableModifier("minecraft:chests/bastion_treasure")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:half_heart"));
    event.addLootTableModifier("minecraft:chests/bastion_treasure")
        .randomChance(0.10).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/bastion_treasure")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:warrior_ritual_tablet"));

    
    // Ruined Portal
    event.addLootTableModifier("minecraft:chests/ruined_portal")
        .randomChance(0.01).addLoot(Item.of("hbs_traveler_rewards:warrior_ritual_tablet"));

    
    // Ancient City
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.25).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.25).addLoot(Item.of("hbs_traveler_rewards:escape_charm"));
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.075).addLoot(Item.of("hbs_traveler_rewards:mob_ward"));
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.125).addLoot(Item.of("hbs_traveler_rewards:diamond_shard"));
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.075).addLoot(LootEntry.of("hbs_traveler_rewards:fabrication_ritual_tablet").setCount([1, 4]));
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.025).addLoot(Item.of("hbs_traveler_rewards:soulbound_ritual_tablet"));
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.1)
        .addLoot(LootEntry.of("hbs_traveler_rewards:diamond_facade_block").setCount([2, 6]));
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.1)
        .addLoot(LootEntry.of("hbs_traveler_rewards:diamond_facade_block").setCount([2, 4]));

    
    // End City
    event.addLootTableModifier("minecraft:chests/end_city_treasure")
        .randomChance(0.25).addLoot(LootEntry.of("hbs_traveler_rewards:enchanted_essence").setCount([4, 16]));
    event.addLootTableModifier("minecraft:chests/end_city_treasure")
        .randomChance(0.25).addLoot(Item.of("hbs_traveler_rewards:escape_charm"));

});