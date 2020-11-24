package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** A factory class to create new beacons. */
public class BeaconFactory {
    private int beaconId = 0; // used for generating unique id for each beacon.

    /**
     * Create new beacon, according to the given parameters.
     * @param initialLocation is the location on board where the beacon is placed.
     * @param movementStrategy determines how the beacon moves.
     * @param owner is the real board that represents the world in which the beacon lives.
     */
    public Beacon createBeacon(Location initialLocation, IMovementStrategy movementStrategy, IAgentOwner owner) {
        checkNotNull(initialLocation);
        checkNotNull(movementStrategy);
        checkNotNull(owner);
        checkArgument(owner.isLocationValid(initialLocation));

        Beacon newBeacon = new Beacon(beaconId++, initialLocation, movementStrategy, owner);
        owner.updateAgentLocation(null, initialLocation, newBeacon);
        return newBeacon;
    }
}