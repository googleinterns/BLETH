package com.google.research.bleth.simulator;

/** Tracing Simulation's Beacon, which moves on the board and transmits its unique static ID each round. */
public class Beacon implements IBeacon {
    private final int id;
    private final IMovementStrategy IMovementStrategy;
    private final Board owner;

    private Location realLocation; // the beacon's location on the real board, changed each time the beacon moves.

    /**
     * Create new Beacon with consecutive serial number.
     * @param id is a unique ID.
     * @param initialLocation is the location on board where the beacon is placed.
     * @param IMovementStrategy determines how the beacon moves.
     * @param owner is the real board that represents the world in which the beacon lives.
     */
    Beacon(int id, Location initialLocation, IMovementStrategy IMovementStrategy, Board owner) {
        this.id = id;
        realLocation = initialLocation;
        this.IMovementStrategy = IMovementStrategy;
        this.owner = owner;
    }

    @Override
    public Location moveTo() {
        return IMovementStrategy.moveTo(owner, realLocation);
    }

    @Override
    public Transmission transmit() {
        return new Transmission(id);
    }

    @Override
    public Location getLocation() {
        return realLocation;
    }

    @Override
    public void move() {
        Location nextMove = moveTo();
        owner.moveAgent(realLocation, nextMove, this);
        realLocation = nextMove;
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
