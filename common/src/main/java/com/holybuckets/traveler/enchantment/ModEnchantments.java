package com.holybuckets.traveler.enchantment;

import com.holybuckets.traveler.Constants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

public class ModEnchantments {

    public static DeferredObject<Enchantment> LASTING;

    public static void register() {
        LASTING = Balm.getRegistries().register( BuiltInRegistries.ENCHANTMENT,
            id -> new LastingEnchantment(),
            id("lasting")
        );
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}