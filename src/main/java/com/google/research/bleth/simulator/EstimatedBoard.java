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

/** The estimated board that the resolver holds. Contains the beacons' estimated locations. */
public class EstimatedBoard extends AbstractBoard {

    /**
     * Create an empty board for storing beacons' estimated locations from the resolver's point of view.
     * @param rows is number of rows.
     * @param cols is number of columns.
     */
    public EstimatedBoard(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public String getType() {
        return "EstimatedBoard";
    }
}
