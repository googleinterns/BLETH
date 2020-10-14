package com.google.research.bleth.simulator;

/** Tracing Simulation's Beacon, which moves on the board and transmits its unique static ID each round. */
public class Beacon extends AbstractAgent implements IBeacon {
    private final int id;

    /**
     * Create new Beacon with consecutive serial number.
     * @param id is a unique ID.
     * @param initialLocation is the location on board where the beacon is placed.
     * @param IMovementStrategy determines how the beacon moves.
     * @param owner is the real board that represents the world in which the beacon lives.
     */
    Beacon(int id, Location initialLocation, IMovementStrategy IMovementStrategy, Board owner) {
        super(initialLocation, IMovementStrategy, owner);
        this.id = id;
    }

    @Override
    public Transmission transmit() {
        return new Transmission(id);
    }

    @Override
    public String getType() {
        return "Beacon";
    }

    @Override
    public int getId() {
        return id;
    }
}
