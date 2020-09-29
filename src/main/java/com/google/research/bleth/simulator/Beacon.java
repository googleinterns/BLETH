package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkNotNull;

/** Tracing Simulation's Beacon, which moves on the board and transmits its unique static ID each round. */
public class Beacon implements IBeacon {
    static int beaconId = 0; // used for generating unique id for each beacon.

    public final int id;
    private final MovementStrategy movementStrategy;
    private final Simulation simulation;

    private Location realLocation; // the beacon's location on the board, changes each time the beacon moves.

    /**
     * Create new Beacon with consecutive serial number.
     * @param initialLocation is the location on board where the agent is placed.
     * @param movementStrategy determines how the agent moves.
     * @param simulation is the world the agent lives in.
     */
    Beacon(Location initialLocation, MovementStrategy movementStrategy, Simulation simulation) {
        checkNotNull(initialLocation);
        checkNotNull(movementStrategy);
        checkNotNull(simulation);
        if (!simulation.getBoard().isLocationValid(initialLocation)) {
            throw new IllegalArgumentException("Invalid Location");
        }

        this.id = beaconId++;
        realLocation = initialLocation;
        simulation.getBoard().placeAgent(realLocation,this);
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