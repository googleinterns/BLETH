// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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