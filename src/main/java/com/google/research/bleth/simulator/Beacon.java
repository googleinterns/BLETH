package com.google.research.bleth.simulator;

/** Tracing Simulation's Beacon, which moves on the board and transmits its unique static ID each round. */
public class Beacon implements IBeacon {
    private final int id;
    private final IMovementStrategy movementStrategy;
    private final AbstractSimulation simulation;

    private Location realLocation; // the beacon's location on the board, changed each time the beacon moves.

    /**
     * Create new Beacon with consecutive serial number.
     * @param id is a unique ID.
     * @param initialLocation is the location on board where the agent is placed.
     * @param movementStrategy determines how the agent moves.
     * @param simulation is the world the agent lives in.
     */
    Beacon(int id, Location initialLocation, IMovementStrategy movementStrategy, AbstractSimulation simulation) {
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
