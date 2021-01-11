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

/** Represents an authorized owner of an agent. */
public interface IAgentOwner {

    /**
     * Update the agent's location on board.
     * @param oldLocation is the agent's current location.
     * @param newLocation is the location where the agent will be placed.
     * @param agent is the Agent which moves from oldLocation to newLocation.
     */
    void updateAgentLocation(Location oldLocation, Location newLocation, IAgent agent);

    /**
     * Check if a location is within the board boundaries.
     * @param location is the location to check if valid.
     * @return true if the location is valid, false otherwise.
     */
    boolean isLocationValid(Location location);
}
