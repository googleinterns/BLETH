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

/** A fixed awakeness strategy for an observer - the observer wakes up at the same difference from the start of the awakeness interval. */
public class FixedAwakenessStrategy implements IAwakenessStrategy {
    private final int awakenessCycleDuration;
    private final int awakenessDuration;
    private int nextAwakeningTime;
    private boolean awake = false;

    /**
     * Create a new fixed awakeness strategy.
     * @param awakenessCycleDuration is the duration of each awakeness cycle. An observer can wake up once in a cycle.
     * @param awakenessDuration is the number of rounds that the observer is awake each time it wakes up.
     * @param firstAwakenessTime is the first time the observer wakes up.
     */
    FixedAwakenessStrategy(int awakenessCycleDuration, int awakenessDuration, int firstAwakenessTime) {
        this.awakenessCycleDuration = awakenessCycleDuration;
        this.awakenessDuration = awakenessDuration;
        this.nextAwakeningTime = firstAwakenessTime;
        if (nextAwakeningTime == 0) {
            awake = true;
        }
    }

    @Override
    public boolean isAwake() {
        return awake;
    }

    @Override
    public void updateAwakenessState(int currentRound) {
        if (awake && currentRound >= nextAwakeningTime + awakenessDuration) {
            awake = false;
            nextAwakeningTime += awakenessCycleDuration;
        }
        if (!awake && currentRound >= nextAwakeningTime) {
            awake = true;
        }
    }
}
