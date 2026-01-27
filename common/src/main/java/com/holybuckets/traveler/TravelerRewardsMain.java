package com.holybuckets.traveler;


import com.holybuckets.foundation.event.EventRegistrar;
import com.holybuckets.foundation.structure.StructureAPI;
import com.holybuckets.traveler.config.ModConfig;
import com.holybuckets.traveler.config.TravelerRewardsConfig;
import com.holybuckets.traveler.core.ManagedTraveler;
import com.holybuckets.traveler.core.AnvilRecipeManager;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.LevelLoadingEvent;
import net.blay09.mods.balm.api.event.server.ServerStartingEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

/**
 * Main instance of the mod, initialize this class statically via commonClass
 * This class will init all major Manager instances and events for the mod
 */
public class TravelerRewardsMain {
    private static boolean DEV_MODE = false;;
    public static TravelerRewardsConfig CONFIG;

    public static TravelerRewardsMain INSTANCE;
    public static final Map<Level, StructureAPI> STRUCTURE_APIS = new HashMap<>();

    public TravelerRewardsMain()
    {
        super();
        INSTANCE = this;
        init();
        // LoggerProject.logInit( "001000", this.getClass().getName() ); // Uncomment if you have a logging system in place
    }

    private void init()
    {

        /*
        Proxy for external APIs which are platform dependent
        this.portalApi = (PortalApi) Balm.platformProxy()
            .withFabric("com.holybuckets.challengetemple.externalapi.FabricPortalApi")
            .withForge("com.holybuckets.challengetemple.externalapi.ForgePortalApi")
            .build();
            */

        //Events
        EventRegistrar registrar = EventRegistrar.getInstance();
        //ChallengeBlockBehavior.init(registrar);
        registrar.registerOnLevelLoad(this::onLevelLoad, EventPriority.Lowest);
        ModConfig.init(registrar);
        ManagedTraveler.init(registrar);

        AnvilRecipeManager.init(registrar);


        //register local events
        registrar.registerOnBeforeServerStarted(this::onServerStarting, EventPriority.Highest);

    }

    private void onServerStarting(ServerStartingEvent e) {
        CONFIG = Balm.getConfig().getActiveConfig(TravelerRewardsConfig.class);
        //this.DEV_MODE = CONFIG.devMode;
        this.DEV_MODE = false;
        STRUCTURE_APIS.clear();
    }

    private void onLevelLoad(LevelLoadingEvent.Load event) {
        if(event.getLevel() instanceof ServerLevel level) {
            STRUCTURE_APIS.put(level, new StructureAPI(level));
        }
    }


}
