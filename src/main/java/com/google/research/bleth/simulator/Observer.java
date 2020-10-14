package com.google.research.bleth.simulator;

import java.util.ArrayList;
import java.util.List;

/** Simulation's Observer, which moves on the board, observe beacons' transmissions and pass the information to its resolver. */
public class Observer extends AbstractAgent implements IObserver {
    private final int id;
    private final IResolver resolver;
    private final IAwakenessStrategy awakenessStrategy;

    private List<Transmission> transmissions = new ArrayList<>(); // contains the transmissions the observer observed in the current round

    /**moveAgent
     * Create new observer with consecutive serial number.
     * @param id is a unique ID.
     * @param initialLocation is the location on board where the observer is placed.
     * @param IMovementStrategy determines how the observer moves.
     * @param resolver is the resolver that the observer belongs to.
     * @param owner is the real board that represents the world in which the observer lives.
     * @param awakenessStrategy determines when the observer wakes up.
     */
    Observer(int id, Location initialLocation, IMovementStrategy IMovementStrategy, IResolver resolver,
             IAgentOwner owner, IAwakenessStrategy awakenessStrategy) {
        super(initialLocation, IMovementStrategy, owner);
        this.id = id;
        this.resolver = resolver;
        this.awakenessStrategy = awakenessStrategy;
    }

    @Override
    public void observe(Transmission beaconTransmission) {
        transmissions.add(beaconTransmission);
    }

    @Override
    public void passInformationToResolver() {
        resolver.receiveInformation(getLocation(), new ArrayList<>(transmissions));
        transmissions.clear();
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