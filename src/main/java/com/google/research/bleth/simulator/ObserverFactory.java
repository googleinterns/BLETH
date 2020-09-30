package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkNotNull;

/** A factory class to create new observers. */
public class ObserverFactory {
    static int observerId = 0; // used for generating unique id for each observer.

    /**
     * Create new observer, according to the given parameters.
     * @param initialLocation is the location on board where the observer is placed.
     * @param movementStrategy determines how the observer moves.
     * @param resolver is the resolver that the observer belongs to.
     * @param simulation is the world the observer lives in.
     * @param awakenessDuration is the duration of each awakeness interval.
     * @param firstAwakenessTime is the first time the observer wakes up.
     * @param awakenessStrategy determines when the observer wakes up.
     * @return
     */
    public Observer createObserver(Location initialLocation, MovementStrategy movementStrategy, IResolver resolver, Simulation simulation,
                                   int awakenessDuration, int firstAwakenessTime, AwakenessStrategy awakenessStrategy) {
        checkNotNull(initialLocation);
        checkNotNull(movementStrategy);
        checkNotNull(resolver);
        checkNotNull(simulation);
        checkNotNull(awakenessStrategy);
        simulation.getBoard().validateLocation(initialLocation);

        Observer newObserver = new Observer(observerId++, initialLocation, movementStrategy, resolver, simulation,
                awakenessDuration, firstAwakenessTime, awakenessStrategy);
        simulation.getBoard().placeAgent(initialLocation, newObserver);
        return newObserver;
    }
}
