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

import static com.google.common.truth.Truth.assertThat;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class AwakenessStrategyTest {
    @Test
    public void observerWithFirstAwakenessTimeZeroStartsAwake() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                        /* awakenessDuration= */ 3,
                                        /* firstAwakenessTime= */ 0);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerWithFirstAwakenessTimeOneStartsAsleep() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                        /* awakenessDuration= */ 3,
                                        /* firstAwakenessTime= */ 1);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerWakesUpAtRoundTwo() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                        /* awakenessDuration= */ 3,
                                        /* firstAwakenessTime= */ 2);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerThatWakesUpAtRoundTwoSleepsAtRoundOne() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                        /* awakenessDuration= */ 3,
                                        /* firstAwakenessTime= */ 2);
        awakenessStrategy.updateAwakenessState(1);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerThatWakesUpAtRoundTwoForTwoRoundsIsAwakeAtRoundThree() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                        /* awakenessDuration= */ 2,
                                        /* firstAwakenessTime= */ 2);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        awakenessStrategy.updateAwakenessState(3);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerThatWakesUpAtRoundOneForTwoRoundsAsleepAtRoundThree() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 10,
                                        /* awakenessDuration= */ 2,
                                        /* firstAwakenessTime= */ 1);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        awakenessStrategy.updateAwakenessState(3);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerThatWakesUpAtRoundZeroAndIsAwakeForAWholeCycleStaysAwake() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 2,
                                        /* awakenessDuration= */ 2,
                                        /* firstAwakenessTime= */ 0);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerWakesUpAtTheSecondCycle() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 2,
                                        /* awakenessDuration= */ 1,
                                        /* firstAwakenessTime= */ 0);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);
        boolean wakesUpAtTwo = awakenessStrategy.isAwake();
        awakenessStrategy.updateAwakenessState(3);
        boolean wakesUpAtThree = awakenessStrategy.isAwake();

        assertThat(wakesUpAtTwo || wakesUpAtThree).isTrue();
    }

    @Test
    public void observerWakesUpExactlyOnceAtTheSecondCycle() {
        IAwakenessStrategy awakenessStrategy =
                createAwakenessStrategy(/* awakenessCycleDuration= */ 2,
                                        /* awakenessDuration= */ 1,
                                        /* firstAwakenessTime= */ 0);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);
        boolean wakesUpAtTwo = awakenessStrategy.isAwake();
        awakenessStrategy.updateAwakenessState(3);
        boolean wakesUpAtThree = awakenessStrategy.isAwake();

        assertThat(wakesUpAtTwo && wakesUpAtThree).isFalse();
    }

    abstract IAwakenessStrategy createAwakenessStrategy(int awakenessCycleDuration, int awakenessDuration, int firstAwakenessTime);
}
