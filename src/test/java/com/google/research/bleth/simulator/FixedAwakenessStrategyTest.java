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
public class FixedAwakenessStrategyTest extends AwakenessStrategyTest {

    @Test
    public void observerThatWakesUpAtRoundOneWakesUpExactlyOneCycleLater() {
        IAwakenessStrategy awakenessStrategy =
                new FixedAwakenessStrategy(/* awakenessCycleDuration= */ 2,
                        /* awakenessDuration= */ 1,
                        /* firstAwakenessTime= */ 1);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        awakenessStrategy.updateAwakenessState(3);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void awakenessTimeIsFixed() {
        boolean wakesUpAtTwo = false;
        boolean wakesUpAtThree = false;

        for (int i = 0; i < 1000; i++) {
            IAwakenessStrategy awakenessStrategy =
                    new FixedAwakenessStrategy(/* awakenessCycleDuration= */ 2,
                            /* awakenessDuration= */ 1,
                            /* firstAwakenessTime= */ 0);
            awakenessStrategy.updateAwakenessState(1);

            awakenessStrategy.updateAwakenessState(2);
            if (awakenessStrategy.isAwake()) {
                wakesUpAtTwo = true;
            }
            awakenessStrategy.updateAwakenessState(3);
            if (awakenessStrategy.isAwake()) {
                wakesUpAtThree = true;
            }
        }
        assertThat(wakesUpAtTwo && !wakesUpAtThree).isTrue();
    }

    FixedAwakenessStrategy createAwakenessStrategy(int awakenessCycleDuration, int awakenessDuration, int firstAwakenessTime) {
        return new FixedAwakenessStrategy(awakenessCycleDuration, awakenessDuration, firstAwakenessTime);
    }
}
