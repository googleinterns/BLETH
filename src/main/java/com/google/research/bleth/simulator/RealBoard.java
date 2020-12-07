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

/** The real board that the simulation holds. Represents the real world and contains the beacons' real locations. */
public class RealBoard extends AbstractBoard implements IAgentOwner {

    /**
     * Create an empty board for storing the real agents' locations in the simulation.
     * @param rows is number of rows.
     * @param cols is number of columns.
     */
    public RealBoard(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public String getType() {
        return "RealBoard";
    }

    @Override
    public void updateAgentLocation(Location oldLocation, Location newLocation, IAgent agent) {
        if (oldLocation == null) {
            placeAgent(newLocation, agent);
        } else {
            moveAgent(oldLocation, newLocation, agent);
        }
    }
}
