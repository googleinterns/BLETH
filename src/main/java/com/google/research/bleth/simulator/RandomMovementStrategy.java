// Copyright 2019 Google LLC
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

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A random movement strategy for an agent. */
public class RandomMovementStrategy implements IMovementStrategy {

    private final static List<Direction> directions =
            ImmutableList.copyOf(Direction.values());

    /**
     * Determine and return the new location of a randomly moving agent, which is walking a single step in one of the
     * possible directions from its current location. If an agent can't move to any direction, return its current location.
     * @param owner is the board that the agent is placed on.
     * @param currentLocation is the current location of the agent on the board.
     * @return the location on the board that the agent is moving to.
     */
    @Override
    public Location moveTo(IAgentOwner owner, Location currentLocation) {
        List<Direction> shuffledDirections = new ArrayList<>(directions);
        Collections.shuffle(shuffledDirections);
        for (Direction direction : shuffledDirections) {
            Location newLocation = currentLocation.moveInDirection(direction);
            if (owner.isLocationValid(newLocation)) {
                return newLocation;
            }
        }
        return currentLocation;
    }
}
