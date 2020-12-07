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

import com.google.auto.value.AutoValue;

/** A location on board. */
@AutoValue
public abstract class Location {
    public static Location create(int row, int col) {
        return new AutoValue_Location(row, col);
    }

    public abstract int row();
    public abstract int col();

    /**
     * Calculate and return the new location based on the current location and the given direction.
     * @param direction is the direction to move to it, one of the four directions: up, down, right and left.
     * @return new Location, differ from the original location in one step, according the given direction.
     */
    public Location moveInDirection(Direction direction) {
        return create(row() + direction.getRowDelta(), col() + direction.getColDelta());
    }
}