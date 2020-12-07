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

/** Represent both observer and beacon. */
public interface IAgent {

    /**
     * Returns the current agent's location on the real board.
     */
    Location getLocation();

    /**
     * Calculate the location the agent is moving to, based on its current location and its strategy.
     * @return the location on board which the agent is moving to.
     */
    Location moveTo();

    /**
     * Move the agent to its next location and update the board accordingly.
     */
    void move();

    /**
     * Returns a string representing of the Agent's type.
     */
    String getType();

    /**
     * Returns the Agent's ID.
     */
    int getId();
}
