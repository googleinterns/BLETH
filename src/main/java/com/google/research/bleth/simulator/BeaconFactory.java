package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkNotNull;

/** A factory class to create new beacons. */
public class BeaconFactory {
    static int beaconId = 0; // used for generating unique id for each beacon.

    /**
     * Create new beacon, according to the given parameters.
     * @param initialLocation is the location on board where the agent is placed.
     * @param movementStrategy determines how the agent moves.
     * @param simulation is the world the agent lives in.
     */
    public Beacon createBeacon(Location initialLocation, IMovementStrategy movementStrategy, Simulation simulation) {
        checkNotNull(initialLocation);
        checkNotNull(movementStrategy);
        checkNotNull(simulation);
        simulation.getBoard().validateLocation(initialLocation);

        Beacon newBeacon = new Beacon(beaconId++, initialLocation, movementStrategy, simulation);
        simulation.getBoard().placeAgent(initialLocation, newBeacon);
        return newBeacon;
    }
}
