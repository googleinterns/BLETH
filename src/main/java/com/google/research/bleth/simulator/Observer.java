package com.google.research.bleth.simulator;

import java.util.ArrayList;
import java.util.List;

/** */
public class Observer implements IObserver {
    private final int id;
    private final MovementStrategy movementStrategy;
    private final IResolver resolver;
    private final Simulation simulation;

    private List<Transmission> transmissions = new ArrayList<Transmission>(); // contains the transmissions the observer observed in the current round

    private Location realLocation; // the observer's location on the board, changed each time the observer moves.

    /**
     * Create new Beacon with consecutive serial number.
     * @param id is a unique ID.
     * @param initialLocation is the location on board where the agent is placed.
     * @param movementStrategy determines how the agent moves.
     * @param resolver is the resolver that the observer belongs to.
     * @param simulation is the world the agent lives in.
     */
    Observer(int id, Location initialLocation, MovementStrategy movementStrategy, IResolver resolver, Simulation simulation) {
        this.id = id;
        realLocation = initialLocation;
        this.movementStrategy = movementStrategy;
        this.resolver = resolver;
        this.simulation = simulation;
    }

    @Override
    public void observe(Transmission beaconTransmission) {
        transmissions.add(beaconTransmission);
    }

    @Override
    public void passInformationToResolver() {
        resolver.receiveInformation(realLocation, transmissions);
    }

    @Override
    public Location moveTo() {
        return movementStrategy.moveTo(simulation.getBoard(), realLocation);
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
        return "Observer";
    }

    @Override
    public int getId() {
        return id;
    }
}
