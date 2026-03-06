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
    static DeferredObject<MobEffect> BLESSING_COOL_BREEZE;
    static DeferredObject<MobEffect> BLESSING_WARM_WINDS;
    static DeferredObject<MobEffect> BLESSING_BUILDERS_FLIGHT;

    public static void register() {
        BLESSING_TRAVELER = Balm.getRegistries().register(
            BuiltInRegistries.MOB_EFFECT,
            id -> new BlessingTravelerEffect(),
            id("blessing_traveler")
        );
        
        BLESSING_COOL_BREEZE = Balm.getRegistries().register(
            BuiltInRegistries.MOB_EFFECT,
            id -> new BlessingCoolBreezeEffect(),
            id("blessing_cool_breeze")
        );
        
        BLESSING_WARM_WINDS = Balm.getRegistries().register(
            BuiltInRegistries.MOB_EFFECT,
            id -> new BlessingWarmWindsEffect(),
            id("blessing_warm_winds")
        );
        
        BLESSING_BUILDERS_FLIGHT = Balm.getRegistries().register(
            BuiltInRegistries.MOB_EFFECT,
            id -> new BlessingBuildersFlightEffect(),
            id("blessing_builders_flight")
        );
    }

    public static class BlessingTravelerEffect extends MobEffect {
        public static final int COLOR = 0x98D982; // Green color
        public static final int DURATION = 60*20*24; //Full minecraft day == 24 minutes == 60*20*24 ticks
        protected BlessingTravelerEffect() {
            super(MobEffectCategory.BENEFICIAL, COLOR);
        }
    }

    public static class BlessingCoolBreezeEffect extends MobEffect {
        public static final int COLOR = 0x5B9BD5; // Blue color
        public static final int DURATION = 60*20*24; //Full minecraft day == 24 minutes == 60*20*24 ticks
        protected BlessingCoolBreezeEffect() {
            super(MobEffectCategory.BENEFICIAL, COLOR);
        }
    }

    public static class BlessingWarmWindsEffect extends MobEffect {
        public static final int COLOR = 0xE8853D; // Orange color
        public static final int DURATION = 60*20*24; //Full minecraft day == 24 minutes == 60*20*24 ticks
        protected BlessingWarmWindsEffect() {
            super(MobEffectCategory.BENEFICIAL, COLOR);
        }
    }

    public static class BlessingBuildersFlightEffect extends MobEffect {
        public static final int COLOR = 0xC8C8D4; // Silver color
        public static final int DURATION = 60*20*24; //Full minecraft day == 24 minutes == 60*20*24 ticks
        protected BlessingBuildersFlightEffect() {
            super(MobEffectCategory.BENEFICIAL, COLOR);
        }
    }

    private static ResourceLocation id(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
