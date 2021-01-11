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

/** Possible directions an agent can move to. */
public enum Direction {
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    UP(-1, 0);

    private final int rowDelta;
    private final int colDelta;

    Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    /** Return the delta of the row index required to move in the specific direction. */
    public int getRowDelta() {
        return rowDelta;
    }

    /** Return the delta of the column index required to move in the specific direction. */
    public int getColDelta() {
        return colDelta;
    }
}
