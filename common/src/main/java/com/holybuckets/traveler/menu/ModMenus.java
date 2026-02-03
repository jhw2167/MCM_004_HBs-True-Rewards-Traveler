package com.holybuckets.traveler.menu;
import com.holybuckets.traveler.Constants;
import net.blay09.mods.balm.api.DeferredObject;
import net.blay09.mods.balm.api.menu.BalmMenus;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

    public static DeferredObject<MenuType<MobWardMenu>> MOB_WARD_MENU;
    public static DeferredObject<MenuType<PotionPotMenu>> POTION_POT_MENU;
    //for weatheed beacon
    public static DeferredObject<MenuType<WeatheredBeaconMenu>> WEATHERED_BEACON;


    public static void initialize(BalmMenus menu) {
        MOB_WARD_MENU = menu.registerMenu(
            new ResourceLocation(Constants.MOD_ID, "mob_ward"),
            MobWardMenu::new
        );

        POTION_POT_MENU = menu.registerMenu(
            new ResourceLocation(Constants.MOD_ID, "potion_pot"),
            PotionPotMenu::new
        );

        WEATHERED_BEACON = menu.registerMenu(
            new ResourceLocation(Constants.MOD_ID, "weathered_beacon"),
            WeatheredBeaconMenu::new
        );
    }
}