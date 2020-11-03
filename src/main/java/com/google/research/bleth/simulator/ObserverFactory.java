package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** A factory class to create new observers. */
public class ObserverFactory {
    static int observerId = 0; // used for generating unique id for each observer.

    /**
     * Create new observer, according to the given parameters.
     * @param initialLocation is the location on board where the observer is placed.
     * @param movementStrategy determines how the observer moves.
     * @param resolver is the resolver that the observer belongs to.
     * @param owner is the real board that represents the world in which the observer lives.
     * @param awakenessStrategy determines when the observer is activated.
     * @return
     */
    public Observer createObserver(Location initialLocation, IMovementStrategy movementStrategy, IResolver resolver,
                                   IAgentOwner owner, IAwakenessStrategy awakenessStrategy) {
        checkNotNull(initialLocation);
        checkNotNull(movementStrategy);
        checkNotNull(resolver);
        checkNotNull(owner);
        checkNotNull(awakenessStrategy);
        checkArgument(owner.isLocationValid(initialLocation));

        Observer newObserver = new Observer(observerId++, initialLocation, movementStrategy, resolver, owner, awakenessStrategy);
        owner.updateAgentLocation(null, initialLocation, newObserver);
        return newObserver;
    }
}
