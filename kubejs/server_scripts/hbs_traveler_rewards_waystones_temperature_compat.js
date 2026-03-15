// priority: 0

// Visit the wiki for more info - https://kubejs.com/


LootJS.modifiers(event => {

    // ── Cool Breeze & Warm Winds - for Tough as Nails climate relief Blessings

    const climateStructures = [
        // Desert
        "minecraft:chests/desert_pyramid",
        // Jungle
        "minecraft:chests/jungle_temple",
        // Snowy
        "minecraft:chests/igloo_chest"
    ];
	  

    climateStructures.forEach(tableId => {
        event.addLootTableModifier(tableId)
            .addWeightedLoot([1, 1], [
                Item.of("hbs_traveler_rewards:blessing_traveler_potion").withChance(2),
            ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);
    });
	
	const warmStructures = [
        "minecraft:chests/desert_pyramid",
        "minecraft:chests/jungle_temple",
		"minecraft:chests/witch_hut"
    ];


    warmStructures.forEach(tableId => {
        event.addLootTableModifier(tableId)
            .addWeightedLoot([1, 1], [
                Item.of("hbs_traveler_rewards:blessing_cool_breeze_potion").withChance(4),
            ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);
    });
	
	
	const coolStructures = [
		"minecraft:chests/igloo_chest",
		"minecraft:chests/witch_hut"
        
    ];
	
	coolStructures.forEach(tableId => {
        event.addLootTableModifier(tableId)
            .addWeightedLoot([1, 1], [
                Item.of("hbs_traveler_rewards:blessing_warm_winds_potion").withChance(4),
            ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);
    });
	
	

    // ── Traveler's Blessing — Waystones travel with all items + climate relief Blessing

    const travelerSpecialStructures = [
        "minecraft:chests/shipwreck_treasure",
        "minecraft:chests/buried_treasure",
        "minecraft:chests/woodland_mansion",
        "minecraft:chests/pillager_outpost",
        "minecraft:chests/witch_hut",
        "minecraft:chests/stronghold_library",
    ];

    travelerSpecialStructures.forEach(tableId => {
        event.addLootTableModifier(tableId)
            .addWeightedLoot([1, 1], [
                Item.of("hbs_traveler_rewards:blessing_traveler_potion").withChance(2),
            ]).addWeightedLoot([1, 1], [Item.of("minecraft:air").withChance(50)]);
    });

  
});