package com.holybuckets.traveler.core;

import com.holybuckets.foundation.HBUtil;
import com.holybuckets.traveler.LoggerProject;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static com.holybuckets.traveler.core.ManagedTraveler.TRAVELERS;

public class ManagedTravelerApi {

    public static final String CLASS_ID = "034";

    private static class ManagedTravelerException extends Exception {
        private final String errorCode;
        
        public ManagedTravelerException(String errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
    }

    public static void clearSoulboundSlots(Player player) {
        try {
            ManagedTraveler mp = getManagedTraveler(player);
            mp.clearSoulboundSlots();
        } catch (ManagedTravelerException e) {
            LoggerProject.logError(e.getErrorCode(), e.getMessage());
        }
    }


    private static ManagedTraveler getManagedTraveler(Player player) throws ManagedTravelerException {
        if(!(player instanceof ServerPlayer)) {
            throw new ManagedTravelerException("034000", "ManagedTravelerApi::getManagedTraveler should only be called on the server side.");
        }
        
        String id = HBUtil.PlayerUtil.getId(player);
        ManagedTraveler mp = TRAVELERS.get(id);
        if(mp == null) {
            throw new ManagedTravelerException("034002", "No ManagedTraveler found for player with id " + id);
        }
        
        return mp;
    }

}
