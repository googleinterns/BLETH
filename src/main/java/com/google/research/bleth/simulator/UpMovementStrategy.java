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

/**
 * A deterministic movement strategy using for testing.
 * Moves the agent up whenever this is a legal move, otherwise stays in the same location.
 */
public class UpMovementStrategy implements IMovementStrategy {

    @Override
    public Location moveTo(IAgentOwner owner, Location currentLocation) {
        Location newLocation = currentLocation.moveInDirection(Direction.UP);
        if (owner.isLocationValid(newLocation)) {
            return newLocation;
        }
        return currentLocation;
    }
}
