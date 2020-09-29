package com.google.research.bleth.simulator;

/**
 * A Class used for testing board visualization.
 */
public class DummyBeacon implements Agent {

    private final int id;
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
    public void move() { }

    @Override
    public String getType() {
        return "Beacon";
    }

    @Override
    public int getId() {
        return id;
    }
}
