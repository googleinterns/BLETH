package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkNotNull;

/** A factory class to create new beacons. */
public class BeaconFactory {
    static int beaconId = 0; // used for generating unique id for each beacon.

    /**
     * Create new beacon, according to the given parameters.
     * @param initialLocation is the location on board where the beacon is placed.
     * @param IMovementStrategy determines how the beacon moves.
     * @param simulation is the world the beacon lives in.
     */
    public Beacon createBeacon(Location initialLocation, IMovementStrategy IMovementStrategy, Simulation simulation) {
        checkNotNull(initialLocation);
        checkNotNull(IMovementStrategy);
        checkNotNull(simulation);
        simulation.getBoard().validateLocation(initialLocation);

        Beacon newBeacon = new Beacon(beaconId++, initialLocation, IMovementStrategy, simulation);
        simulation.getBoard().placeAgent(initialLocation, newBeacon);
        return newBeacon;
    }
}
