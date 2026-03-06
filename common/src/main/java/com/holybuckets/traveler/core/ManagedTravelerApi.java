package com.holybuckets.traveler.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.traveler.LoggerProject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static com.holybuckets.traveler.core.ManagedTraveler.TRAVELERS;

public class ManagedTravelerApi {

    public static final String CLASS_ID = "034";

    public static void clearSoulboundSlots(Player player) {
        if(player instanceof ServerPlayer) {
            String id = HBUtil.PlayerUtil.getId(player);
            ManagedTraveler mp = TRAVELERS.get(id);
            mp.clearSoulboundSlots();
        } else {
            LoggerProject.logError("034000", "ManagedTravelerApi::clearSoulboundSlots should only be called on the server side.");
        }
    }

}
