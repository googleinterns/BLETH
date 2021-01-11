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

import java.util.Random;

/**
 * A class for generating strategies based on awakeness cycle and duration, and strategy type provided.
 * The factory gets as an argument the specific type of strategy to generate.
 * The factory sets these constants for each generated strategy, and generates random parameters (if such exists).
 */
public class AwakenessStrategyFactory {

    /** An enum representing an awakeness strategy type. */
    public enum Type {
        FIXED(false),
        RANDOM(false);

        private final boolean isForTest;

        Type(boolean isForTest) { this.isForTest = isForTest; }

        /** Return true if this is a strategy for test only usage, and false otherwise. */
        boolean isForTest() { return isForTest; }
    }

    private Random rand = new Random(); // used for generating the initial awakeness time
    private Type type;

    /**
     * Create new AwakenessStrategyFactory.
     * @param type is the type of the awakeness strategies generated using the created factory.
     */
    public AwakenessStrategyFactory(Type type) {
        this.type = type;
    }

    /**
     * Create a new strategy according to the factory's type attribute and the following params:
     * @param awakenessCycle is the cycle, which is the number of rounds in which every observer
     * must have an awakeness period.
     * @param awakenessDuration is the duration, which is the number of rounds in which an observer
     * is awake in a single awakeness cycle.
     * @return an awakeness strategy initialized with all required parameters (if no proper type was set, return random by default).
     */
    public IAwakenessStrategy createStrategy(int awakenessCycle, int awakenessDuration) {
        switch (this.type) {
            case FIXED: return createFixedStrategy(awakenessCycle, awakenessDuration);
            default: return createRandomStrategy(awakenessCycle, awakenessDuration);
        }
    }

    private IAwakenessStrategy createFixedStrategy(int awakenessCycle, int awakenessDuration) {
        int firstAwakenessTime = rand.nextInt(awakenessCycle - awakenessDuration + 1);
        return new FixedAwakenessStrategy(awakenessCycle, awakenessDuration, firstAwakenessTime);
    }

    private IAwakenessStrategy createRandomStrategy(int awakenessCycle, int awakenessDuration) {
        int firstAwakenessTime = rand.nextInt(awakenessCycle - awakenessDuration + 1);
        return new RandomAwakenessStrategy(awakenessCycle, awakenessDuration, firstAwakenessTime);
    }
}