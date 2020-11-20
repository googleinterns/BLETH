package com.google.research.bleth.simulator;

import java.util.List;

/**
 * A fake implementation for IResolver that allows to test the arguments it receives when an observer calls receiveInformation.
 */
class FakeResolver implements IResolver {
    private Location observerLocation;
    private List<Transmission> transmissions;

    @Override
    public void receiveInformation(Location observerLocation, List<Transmission> transmissions) {
        this.observerLocation = observerLocation;
        this.transmissions = transmissions;
    }

    public Location getObserverLocation() {
        return observerLocation;
    }

    public List<Transmission> getTransmissions() {
        return transmissions;
    }

    public AbstractBoard getBoard() {
        return null;
    }
}
