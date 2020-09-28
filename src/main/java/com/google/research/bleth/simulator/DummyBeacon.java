package com.google.research.bleth.simulator;

/**
 * A CLass used only for testing board visualization.
 */
public class DummyBeacon implements Agent {

    public final int id;
    private Location realLocation;

    public DummyBeacon(int id, Location realLocation) {
        this.id = id;
        this.realLocation = realLocation;
    }

    @Override
    public Location getLocation() {
        return this.realLocation;
    }

    @Override
    public Location moveTo() {
        return this.realLocation;
    }

    @Override
    public void move() {

    }

    @Override
    public String getTypeAndIdAsString() {
        return 'b' + Integer.toString(this.id);
    }
}
