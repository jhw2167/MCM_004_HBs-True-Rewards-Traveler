package com.holybuckets.traveler.client;

import com.holybuckets.foundation.client.ClientBalmEventRegister;
import com.holybuckets.foundation.client.ClientEventRegistrar;
import com.holybuckets.foundation.client.MessagerClient;
import com.holybuckets.foundation.event.custom.TickType;
import com.holybuckets.foundation.player.ManagedPlayer;
import com.holybuckets.foundation.structure.StructureManager;
import com.holybuckets.traveler.client.screen.ModScreens;
import com.holybuckets.traveler.core.ManagedTraveler;
import com.holybuckets.traveler.menu.ModMenus;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.api.event.EventPriority;
import net.blay09.mods.balm.api.event.client.BlockHighlightDrawEvent;
import net.blay09.mods.balm.api.event.client.ConnectedToServerEvent;
import net.blay09.mods.balm.api.event.server.ServerStoppedEvent;
import net.minecraft.client.Minecraft;
import com.holybuckets.traveler.client.render.SoulboundSlotRenderer;

public class CommonClassClient {


    public static void initClient() {
        initClientEvents();
        initRenderers();
        //testRenderers();
        ModScreens.initialize(BalmClient.getScreens());
    }

    //** CLIENT INITIALIZERS **//
    private static void initClientEvents() {
        ClientEventRegistrar reg = ClientEventRegistrar.getInstance();
        reg.registerOnGuiDrawPost(SoulboundSlotRenderer::renderHotbarSoulboundIndicators);
        reg.registerOnContainerScreenDrawForeground(SoulboundSlotRenderer::renderInventorySoulboundIndicators);

        ClientBalmEventRegister.registerEvents();
    }


    private static void initRenderers() {
        ModRenderers.clientInitialize(BalmClient.getRenderers());
    }



    //** Tests

}
