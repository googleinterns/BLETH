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

/** Represent both observer and beacon. Responsible for the agents' movement. */
public abstract class AbstractAgent implements IAgent {
    private final IMovementStrategy IMovementStrategy;
    private final IAgentOwner owner;
    private Location realLocation; // the agent's location on the board, changed each time the agent moves.

    /**
     * @param initialLocation is the location on board where the agent is placed.
     * @param movementStrategy determines how the agent moves.
     * @param owner is the real board that represents the world in which the agent lives.
     */
    public AbstractAgent(Location initialLocation, IMovementStrategy movementStrategy, IAgentOwner owner) {
        realLocation = initialLocation;
        this.IMovementStrategy = movementStrategy;
        this.owner = owner;
    }

    @Override
    public Location getLocation() {
        return realLocation;
    }

    /**
     * Calculate the location the agent is moving to, based on its current location and its strategy.
     * @return the location on board which the agent is moving to.
     */
    @Override
    public Location moveTo() {
        return IMovementStrategy.moveTo(owner, realLocation);
    }

    @Override
    public void move() {
        Location nextMove = moveTo();
        owner.updateAgentLocation(realLocation, nextMove, this);
        realLocation = nextMove;
    }
}
