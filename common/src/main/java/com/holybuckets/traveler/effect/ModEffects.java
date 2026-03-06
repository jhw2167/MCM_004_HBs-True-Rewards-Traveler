package com.holybuckets.traveler.effect;

import com.holybuckets.traveler.Constants;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.DeferredObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class ModEffects {

    static DeferredObject<MobEffect> BLESSING_TRAVELER;


    public static void register() {
        BLESSING_TRAVELER = Balm.getRegistries().register(
            BuiltInRegistries.MOB_EFFECT,
            id -> new BlessingTravelerEffect(),
            id("blessing_traveler")
        );
    }

    public static class BlessingTravelerEffect extends MobEffect {
        public static final int COLOR = 0x98D982; // Green color
        public static final int DURATION = 60*20*24; //Full minecraft day == 24 minutes == 60*20*24 ticks
        protected BlessingTravelerEffect() {
            super(MobEffectCategory.BENEFICIAL, COLOR);
        }
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
