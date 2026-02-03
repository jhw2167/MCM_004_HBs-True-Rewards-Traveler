package com.holybuckets.traveler.client.screen;

import com.holybuckets.traveler.menu.ModMenus;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.client.screen.BalmScreens;

public class ModScreens {

    public static void initialize(BalmScreens screens) {
        screens.registerScreen(
            ModMenus.MOB_WARD_MENU::get,
            MobWardScreen::new
        );

        screens.registerScreen(
            ModMenus.POTION_POT_MENU::get,
            PotionPotScreen::new
        );

        screens.registerScreen(
            ModMenus.WEATHERED_BEACON::get,
            WeatheredBeaconScreen::new
        );
    }
}