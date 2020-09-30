package com.google.research.bleth.simulator;

/** Tracing Simulation's Beacon, which moves on the board and transmits its unique static ID each round. */
public class Beacon implements IBeacon {
    private final int id;
    private final MovementStrategy movementStrategy;
    private final Simulation simulation;

    private Location realLocation; // the beacon's location on the board, changed each time the beacon moves.

    /**
     * Create new Beacon with consecutive serial number.
     * @param id is a unique ID.
     * @param initialLocation is the location on board where the beacon is placed.
     * @param movementStrategy determines how the beacon moves.
     * @param simulation is the world the beacon lives in.
     */
    Beacon(int id, Location initialLocation, MovementStrategy movementStrategy, Simulation simulation) {
        this.id = id;
        realLocation = initialLocation;
        this.movementStrategy = movementStrategy;
        this.simulation = simulation;
    }

    @Override
    public Location moveTo() {
        return movementStrategy.moveTo(simulation.getBoard(), realLocation);
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
        simulation.getBoard().moveAgent(realLocation, nextMove, this);
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
