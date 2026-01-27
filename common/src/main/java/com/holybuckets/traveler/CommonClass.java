package com.holybuckets.traveler;

import com.holybuckets.foundation.event.BalmEventRegister;
import com.holybuckets.traveler.block.ModBlocks;
import com.holybuckets.traveler.block.be.ModBlockEntities;
import com.holybuckets.traveler.client.ModRenderers;
import com.holybuckets.traveler.config.TravelerRewardsConfig;
import com.holybuckets.traveler.item.ModItems;
import com.holybuckets.traveler.menu.ModMenus;
import com.holybuckets.traveler.platform.Services;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.client.BalmClient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Items;


public class CommonClass {

    public static boolean isInitialized = false;
    public static void init()
    {
        if (isInitialized)
            return;

        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", com.holybuckets.traveler.platform.Services.PLATFORM.getPlatformName(), com.holybuckets.traveler.platform.Services.PLATFORM.getEnvironmentName());
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        //Initialize Foundations
        com.holybuckets.foundation.FoundationInitializers.commonInitialize();

        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {
            Constants.LOG.info("Hello to " + Constants.MOD_NAME + "!");
        }

        //RegisterConfigs
        Balm.getConfig().registerConfig(TravelerRewardsConfig.class);
        TravelerRewardsMain.INSTANCE = new TravelerRewardsMain();
        BalmEventRegister.registerEvents();
        BalmEventRegister.registerCommands();
        ModBlocks.initialize(Balm.getBlocks());
        ModBlockEntities.initialize(Balm.getBlockEntities());
        ModItems.initialize(Balm.getItems());
        ModMenus.initialize(Balm.getMenus());

        
        isInitialized = true;
    }

    /**
     * Description: Run sample tests methods
     */
    public static void sample()
    {

    }

    public static void initClient() {
        ModRenderers.clientInitialize(BalmClient.getRenderers());
    }
}