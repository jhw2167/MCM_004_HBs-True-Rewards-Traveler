// priority: 0

// Visit the wiki for more info - https://kubejs.com/


LootJS.modifiers((event) => {

    // Returns a single-item weighted loot pool array with air dilution built in
    function diluted(itemId, chance, count) {
        if(count === undefined) count = 1;
        return [Item.of(itemId, count).withChance(chance)];
    }


    // Village (blacksmith chests)
    event.addLootTableModifier("minecraft:chests/village/village_weaponsmith")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:whetstone", 2));

    event.addLootTableModifier("minecraft:chests/village/village_armorer")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:whetstone", 2));

    event.addLootTableModifier("minecraft:chests/village/village_toolsmith")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:whetstone", 2));


    // Mineshaft
    event.addLootTableModifier("minecraft:chests/abandoned_mineshaft")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(5),
            Item.of("hbs_traveler_rewards:bracing").withChance(5),
            Item.of("hbs_traveler_rewards:diamond_shard").withChance(2)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 4, 2))
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:potion_pot", 10));


    // Dungeon
    event.addLootTableModifier("minecraft:chests/simple_dungeon")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:quarter_heart").withChance(2),
            Item.of("hbs_traveler_rewards:diamond_shard").withChance(2)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:potion_pot", 10))
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:iron_facade_block").withChance(3),
            Item.of("hbs_traveler_rewards:gold_facade_block").withChance(3)
        ]);


    // Desert Pyramid
    event.addLootTableModifier("minecraft:chests/desert_pyramid")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:quarter_heart").withChance(2),
            Item.of("hbs_traveler_rewards:half_heart").withChance(1),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2),
            Item.of(("hbs_traveler_rewards:potion_pot")).withChance(5)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 4, 2))
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 4, 4))
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:iron_facade_block").withChance(3),
            Item.of("hbs_traveler_rewards:gold_facade_block").withChance(3)
        ])
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:diamond_facade_block").withChance(1),
            Item.of("hbs_traveler_rewards:emerald_facade_block").withChance(1)
        ]);


    // Jungle Temple
    event.addLootTableModifier("minecraft:chests/jungle_temple")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:quarter_heart").withChance(2),
            Item.of("hbs_traveler_rewards:half_heart").withChance(1),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 4, 4))
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:potion_pot", 3))
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:iron_facade_block").withChance(3),
            Item.of("hbs_traveler_rewards:gold_facade_block").withChance(3)
        ])
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:diamond_facade_block").withChance(1),
            Item.of("hbs_traveler_rewards:emerald_facade_block").withChance(1)
        ]);


    // Buried Treasure
    event.addLootTableModifier("minecraft:chests/buried_treasure")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:quarter_heart", 2))
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 4));


    // Stronghold
    event.addLootTableModifier("minecraft:chests/stronghold_library")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:weathered_beacon", 4));


    // Pillager Outpost
    event.addLootTableModifier("minecraft:chests/pillager_outpost")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:empty_totem").withChance(3)
        ]);


    // Woodland Mansion
    event.addLootTableModifier("minecraft:chests/woodland_mansion")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:empty_totem").withChance(3),
            Item.of("hbs_traveler_rewards:weathered_beacon").withChance(5)
        ]);


    // Nether Fortress
    event.addLootTableModifier("minecraft:chests/nether_bridge")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2),
            Item.of("hbs_traveler_rewards:diamond_shard").withChance(3),
            Item.of("hbs_traveler_rewards:potion_pot").withChance(5)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 2))
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 8))
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:potion_pot", 3));


    // Bastion - common chests
    event.addLootTableModifier("minecraft:chests/bastion_other")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2),
            Item.of("hbs_traveler_rewards:potion_pot").withChance(5)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 2))
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 8));

    event.addLootTableModifier("minecraft:chests/bastion_bridge")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 2))
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 8));

    event.addLootTableModifier("minecraft:chests/bastion_hoglin_stable")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:whetstone").withChance(2),
            Item.of("hbs_traveler_rewards:escape_charm").withChance(2),
            Item.of("hbs_traveler_rewards:potion_pot").withChance(5)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 2))
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 2, 8));


    // Bastion - treasure room
    event.addLootTableModifier("minecraft:chests/bastion_treasure")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:half_heart").withChance(2),
            Item.of("hbs_traveler_rewards:warrior_ritual_tablet").withChance(2)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 5, 2))
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 1, 16));


    // Ruined Portal
    event.addLootTableModifier("minecraft:chests/ruined_portal")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:warrior_ritual_tablet", 1));


    // Ancient City
    event.addLootTableModifier("minecraft:chests/ancient_city")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], [
            Item.of("hbs_traveler_rewards:escape_charm").withChance(5),
            Item.of("hbs_traveler_rewards:mob_ward").withChance(3),
            Item.of("hbs_traveler_rewards:diamond_shard").withChance(3),
            Item.of("hbs_foundation:enchanted_essence", 2).withChance(3),
            Item.of("hbs_foundation:enchanted_essence", 8).withChance(3)
        ])
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:fabrication_ritual_tablet", 4))
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:diamond_facade_block", 2))
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:soulbound_ritual_tablet", 1));


    // End City
    event.addLootTableModifier("minecraft:chests/end_city_treasure")
        .randomChance(0.1)
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:escape_charm", 5))
        .addWeightedLoot([1, 1], diluted("hbs_foundation:enchanted_essence", 5, 4))
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:soulbound_ritual_tablet", 4))
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:diamond_facade_block", 2, 2))
        .addWeightedLoot([1, 1], diluted("hbs_traveler_rewards:emerald_facade_block", 2));

});