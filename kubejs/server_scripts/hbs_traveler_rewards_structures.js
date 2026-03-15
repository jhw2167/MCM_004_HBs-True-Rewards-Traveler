// priority: 0

// Visit the wiki for more info - https://kubejs.com/


LootJS.modifiers((event) => {

    // Village (blacksmith chests)
    event.addLootTableModifier("minecraft:chests/village/village_weaponsmith")
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:whetstone").withChance(2)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);

    event.addLootTableModifier("minecraft:chests/village/village_armorer")
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:whetstone").withChance(2)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);

    event.addLootTableModifier("minecraft:chests/village/village_toolsmith")
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:whetstone").withChance(2)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Mineshaft
    event.addLootTableModifier("minecraft:chests/abandoned_mineshaft")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(5),
            Item.of("hbs_traveler_rewards:bracing").withChance(5),
            Item.of("hbs_traveler_rewards:diamond_shard").withChance(2)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 2).withChance(4)])
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:potion_pot").withChance(10)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Dungeon
    event.addLootTableModifier("minecraft:chests/simple_dungeon")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:quarter_heart").withChance(2),
            Item.of("hbs_traveler_rewards:diamond_shard").withChance(2)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:potion_pot").withChance(10)])
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:iron_facade_block").withChance(3),
            Item.of("hbs_traveler_rewards:gold_facade_block").withChance(3)
        ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Desert Pyramid
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:quarter_heart").withChance(2),
            Item.of("hbs_traveler_rewards:half_heart").withChance(1),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 2).withChance(4)])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 4).withChance(4)])
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:potion_pot").withChance(3)])
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:iron_facade_block").withChance(3),
            Item.of("hbs_traveler_rewards:gold_facade_block").withChance(3)
        ])
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:diamond_facade_block").withChance(1),
            Item.of("hbs_traveler_rewards:emerald_facade_block").withChance(1)
        ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Jungle Temple
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:quarter_heart").withChance(2),
            Item.of("hbs_traveler_rewards:half_heart").withChance(1),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 4).withChance(4)])
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:potion_pot").withChance(3)])
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:iron_facade_block").withChance(3),
            Item.of("hbs_traveler_rewards:gold_facade_block").withChance(3)
        ])
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:diamond_facade_block").withChance(1),
            Item.of("hbs_traveler_rewards:emerald_facade_block").withChance(1)
        ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Buried Treasure
    event.addLootTableModifier("minecraft:chests/buried_treasure")
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:quarter_heart").withChance(2)])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 4).withChance(2)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Stronghold
    event.addLootTableModifier("minecraft:chests/stronghold_library")
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:weathered_beacon").withChance(4)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Pillager Outpost
    event.addLootTableModifier("minecraft:chests/pillager_outpost")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:empty_totem").withChance(3)
        ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Woodland Mansion
    event.addLootTableModifier("minecraft:chests/woodland_mansion")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:empty_totem").withChance(3),
            Item.of("hbs_traveler_rewards:weathered_beacon").withChance(5)
        ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Nether Fortress
    event.addLootTableModifier("minecraft:chests/nether_bridge")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2),
            Item.of("hbs_traveler_rewards:diamond_shard").withChance(3),
            Item.of("hbs_traveler_rewards:potion_pot").withChance(5)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 2).withChance(2)])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 8).withChance(2)])
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:potion_pot").withChance(3)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Bastion - common chests
    event.addLootTableModifier("minecraft:chests/bastion_other")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2),
            Item.of("hbs_traveler_rewards:potion_pot").withChance(5)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 2).withChance(2)])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 8).withChance(2)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);

    event.addLootTableModifier("minecraft:chests/bastion_bridge")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2),
            
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 2).withChance(2)])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 8).withChance(2)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);

    event.addLootTableModifier("minecraft:chests/bastion_hoglin_stable")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2),
            Item.of("hbs_traveler_rewards:potion_pot").withChance(5)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 2).withChance(2)])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 8).withChance(2)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Bastion - treasure room
    event.addLootTableModifier("minecraft:chests/bastion_treasure")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:half_heart").withChance(2),
            Item.of("hbs_traveler_rewards:warrior_ritual_tablet").withChance(2)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 2).withChance(5)])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 16).withChance(1)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Ruined Portal
    event.addLootTableModifier("minecraft:chests/ruined_portal")
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:warrior_ritual_tablet").withChance(1)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);


    // Ancient City
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:escape_charm").withChance(5),
            Item.of("hbs_traveler_rewards:mob_ward").withChance(3),
            Item.of("hbs_traveler_rewards:diamond_shard").withChance(3),
            Item.of("hbs_foundation:enchanted_essence", 2).withChance(3),
            Item.of("hbs_foundation:enchanted_essence", 8).withChance(3)
        ])
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:fabrication_ritual_tablet").withChance(4)])
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:diamond_facade_block").withChance(2)])
        .addWeightedLoot([1, 1], Item.of("hbs_traveler_rewards:soulbound_ritual_tablet").withChance(1));
      


    // End City
    event.addLootTableModifier("minecraft:chests/end_city_treasure")
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:escape_charm").withChance(5)])
        .addWeightedLoot([1, 1], [Item.of("hbs_foundation:enchanted_essence", 4).withChance(5)])
        .addWeightedLoot([1, 1], Item.of("hbs_traveler_rewards:soulbound_ritual_tablet").withChance(4))
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:diamond_facade_block", 2).withChance(2)])
        .addWeightedLoot([1, 1], [Item.of("hbs_traveler_rewards:emerald_facade_block").withChance(2)]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);

});