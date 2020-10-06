package com.google.research.bleth.simulator;

import java.util.ArrayList;
import java.util.List;

/** Simulation's Observer, which moves on the board, observe beacons' transmissions and pass the information to its resolver. */
public class Observer implements IObserver {
    private final int id;
    private final IMovementStrategy IMovementStrategy;
    private final IResolver resolver;
    private final Simulation simulation;
    private final IAwakenessStrategy awakenessStrategy;

    private Location realLocation; // the observer's location on the board, changed each time the observer moves.
    private List<Transmission> transmissions = new ArrayList<Transmission>(); // contains the transmissions the observer observed in the current round

    /**
     * Create new Beacon with consecutive serial number.
     * @param id is a unique ID.
     * @param initialLocation is the location on board where the agent is placed.
     * @param IMovementStrategy determines how the observer moves.
     * @param resolver is the resolver that the observer belongs to.
     * @param simulation is the world the agent lives in.
     * @param awakenessStrategy determines when the observer wakes up.
     */
    Observer(int id, Location initialLocation, IMovementStrategy IMovementStrategy, IResolver resolver,
             Simulation simulation, IAwakenessStrategy awakenessStrategy) {
        this.id = id;
        realLocation = initialLocation;
        this.IMovementStrategy = IMovementStrategy;
        this.resolver = resolver;
        this.simulation = simulation;
        this.awakenessStrategy = awakenessStrategy;
    }

    @Override
    public void observe(Transmission beaconTransmission) {
        transmissions.add(beaconTransmission);
    }

    @Override
    public void passInformationToResolver() {
        resolver.receiveInformation(realLocation, new ArrayList<>(transmissions));
        transmissions.clear();
    }

    @Override
    public Location moveTo() {
        return IMovementStrategy.moveTo(simulation.getBoard(), realLocation);
    }

    @Override
    public Location getLocation() {
        return realLocation;
    }

    @Override
    public void move() {
        Location nextMove = moveTo();
        simulation.getBoard().moveAgent(realLocation, nextMove,this);
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

    @Override
    public boolean isAwake() {
        return awakenessStrategy.isAwake();
    }

    /**
     * Activate the observer if the current round is the start of its current awakeness time
     * and turn it off if it's the end of its current awakeness time.
     * The function is called for every round between 1 and the last round without skipping.
     * @param currentRound is the the current round of the simulation.
     */
    public void updateAwakenessState(int currentRound) {
        awakenessStrategy.updateAwakenessState(currentRound);
    }
}
