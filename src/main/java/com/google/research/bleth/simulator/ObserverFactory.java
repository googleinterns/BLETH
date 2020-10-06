package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkNotNull;

/** A factory class to create new observers. */
public class ObserverFactory {
    static int observerId = 0; // used for generating unique id for each observer.

    /**
     * Create new observer, according to the given parameters.
     * @param initialLocation is the location on board where the observer is placed.
     * @param IMovementStrategy determines how the observer moves.
     * @param resolver is the resolver that the observer belongs to.
     * @param simulation is the world the observer lives in.
     * @param awakenessStrategy determines when the observer is activated.
     * @return
     */
    public Observer createObserver(Location initialLocation, IMovementStrategy IMovementStrategy, IResolver resolver,
                                   Simulation simulation, IAwakenessStrategy awakenessStrategy) {
        checkNotNull(initialLocation);
        checkNotNull(IMovementStrategy);
        checkNotNull(resolver);
        checkNotNull(simulation);
        checkNotNull(awakenessStrategy);
        simulation.getBoard().validateLocation(initialLocation);

        Observer newObserver = new Observer(observerId++, initialLocation, IMovementStrategy, resolver, simulation, awakenessStrategy);
        simulation.getBoard().placeAgent(initialLocation, newObserver);
        return newObserver;
    }
}
