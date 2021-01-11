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

/** Tracing Simulation's Beacon, which moves on the board and transmits its unique static ID each round. */
public class Beacon extends AbstractAgent implements IBeacon {
    private final int id;

    /**
     * Create new Beacon with consecutive serial number.
     * @param id is a unique ID.
     * @param initialLocation is the location on board where the beacon is placed.
     * @param movementStrategy determines how the beacon moves.
     * @param owner is the real board that represents the world in which the beacon lives.
     */
    Beacon(int id, Location initialLocation, IMovementStrategy movementStrategy, IAgentOwner owner) {
        super(initialLocation, movementStrategy, owner);
        this.id = id;
    }

    @Override
    public Transmission transmit() {
        return Transmission.create(id); // In the tracing simulation, the beacon's eid is its Id.
    }

    @Override
    public String getType() {
        return "Beacon";
    }

    @Override
    public int getId() {
        return id;
    }
}