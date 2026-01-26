package com.holybuckets.traveler.client;

import com.holybuckets.foundation.client.ClientBalmEventRegister;
import com.holybuckets.foundation.client.ClientEventRegistrar;
import com.holybuckets.foundation.client.MessagerClient;
import com.holybuckets.foundation.event.custom.TickType;
import com.holybuckets.foundation.player.ManagedPlayer;
import com.holybuckets.foundation.structure.StructureManager;
import com.holybuckets.traveler.core.ManagedTraveler;
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
    }

    //** CLIENT INITIALIZERS **//
    private static void initClientEvents() {
        ClientEventRegistrar reg = ClientEventRegistrar.getInstance();
        reg.registerOnGuiDrawPost(SoulboundSlotRenderer::renderHotbarSoulboundIndicators);
        reg.registerOnContainerScreenDrawForeground(SoulboundSlotRenderer::renderInventorySoulboundIndicators);

        ClientBalmEventRegister.registerEvents();
    }

    public static void initManagedPlayer(ClientEventRegistrar reg) {
    reg.registerOnConnectedToServer( e -> ManagedPlayer.onClientConnectedToServer(
        Minecraft.getInstance().player), EventPriority.Highest);
    }

    public static void initStructureManager(ClientEventRegistrar reg) {
        reg.registerOnClientTick(TickType.ON_120_TICKS ,
             e -> StructureManager.fireSyncClientStructureCountsToServer(Minecraft.getInstance().player));
             /*
        reg.registerOnConnectedToServer(
             e -> StructureManager.onConnectedToServer(Minecraft.getInstance().player));
        StructureManager.clientInit();
              */
    }

    private static void initRenderers() {
        ModRenderers.clientInitialize(BalmClient.getRenderers());
    }



    //** Tests

}
