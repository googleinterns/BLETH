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

/** Represent the awakeness strategy of an observer. */
public interface IAwakenessStrategy {

    /**
     * Activate the observer if the current round is the start of its current awakeness time
     * and turn it off if it's the end of its current awakeness time.
     * The function is called for every round between 1 and the last round without skipping.
     * @param currentRound is the the current round of the simulation.
     */
    void updateAwakenessState(int currentRound);

    /** Returns true if the observer is activated, false otherwise. */
    boolean isAwake();
}