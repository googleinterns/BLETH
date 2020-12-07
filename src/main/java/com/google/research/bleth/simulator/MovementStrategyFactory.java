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

/**
 * A class for generating movement strategies based on strategy type provided.
 * The factory gets as an argument the specific type of strategy to generate.
 */
public class MovementStrategyFactory {

    /** An enum representing a movement strategy type. */
    public enum Type {
        STATIONARY(false),
        RANDOM(false),
        UP(true); // a deterministic movement strategy used for testing.

        private final boolean isForTest;

        Type(boolean isForTest) { this.isForTest = isForTest; }

        /** Return true if this is a strategy for test only usage, and false otherwise. */
        boolean isForTest() { return isForTest; }
    }

    private Type type;

    /**
     * Create new MovementStrategyFactory.
     * @param type is the type of the movement strategies generated using the created factory.
     */
    public MovementStrategyFactory(Type type) {
        this.type = type;
    }

    /**
     * Create a new strategy according to the factory's type attribute.
     * @return a movement strategy (if no proper type was set, return random by default).
     */
    public IMovementStrategy createStrategy() {
        switch (this.type) {
            case STATIONARY: return createStationaryStrategy();
            case UP: return createUpStrategy();
            default: return createRandomStrategy();
        }
    }

    private IMovementStrategy createStationaryStrategy() {
        return new StationaryMovementStrategy();
    }

    private IMovementStrategy createUpStrategy() {
        return new UpMovementStrategy();
    }

    private IMovementStrategy createRandomStrategy() {
        return new RandomMovementStrategy();
    }
}
