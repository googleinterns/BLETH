package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RandomAwakenessStrategyTest extends AwakenessStrategyTest {

    @Test
    public void awakenessTimeIsNotFixed() {
        boolean wakesUpAtTwo = false;
        boolean wakesUpAtThree = false;

        for (int i = 0; i < 1000; i++) {
            IAwakenessStrategy awakenessStrategy = new RandomAwakenessStrategy(2, 1, 0);
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

        assertThat(wakesUpAtTwo && wakesUpAtThree).isTrue();
    }

    IAwakenessStrategy createAwakenessStrategy(int awakenessCycleDuration, int awakenessDuration, int firstAwakenessTime) {
        return new RandomAwakenessStrategy(awakenessCycleDuration, awakenessDuration, firstAwakenessTime);
    }
}
