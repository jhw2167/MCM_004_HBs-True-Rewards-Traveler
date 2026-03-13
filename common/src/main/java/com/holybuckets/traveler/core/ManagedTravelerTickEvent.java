package com.holybuckets.traveler.core;

import net.blay09.mods.balm.api.event.BalmEvent;

public class ManagedTravelerTickEvent extends BalmEvent {

    private final ManagedTraveler managedTraveler;

    public ManagedTravelerTickEvent(ManagedTraveler managedTraveler) {
        this.managedTraveler = managedTraveler;
    }

    public ManagedTraveler getManagedTraveler() {
        return managedTraveler;
    }
}