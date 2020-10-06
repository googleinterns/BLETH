package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FixedAwakenessStrategyTest extends AwakenessStrategyTest {

    @Test
    public void observerWithFirstAwakenessTimeZeroStartsAwake() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(5, 3, 0);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerWithFirstAwakenessTimeOneStartsAsleep() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(5, 3, 1);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerWakesUpAtRoundTwo() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(5,3 ,2);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerThatWakesUpAtRoundTwoSleepsAtRoundOne() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(5, 3, 2);

        awakenessStrategy.updateAwakenessState(1);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerThatWakesUpAtRoundTwoForTwoRoundsIsAwakeAtRoundThree() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(5,2, 2);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        awakenessStrategy.updateAwakenessState(3);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerThatWakesUpAtRoundOneForTwoRoundsAsleepAtRoundThree() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(10, 2, 1);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        awakenessStrategy.updateAwakenessState(3);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerThatWakesUpAtRoundOneWakesUpExactlyOneCycleLater() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(2, 1, 1);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        awakenessStrategy.updateAwakenessState(3);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerThatWakesUpAtRoundZeroAndIsAwakeForAWholeCycleStaysAwake() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(2, 2, 0);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerWakesUpAtTheSecondCycle() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(2, 1, 0);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);
        boolean wakesUpAtTwo = awakenessStrategy.isAwake();
        awakenessStrategy.updateAwakenessState(3);
        boolean wakesUpAtThree = awakenessStrategy.isAwake();

        assertThat(wakesUpAtTwo || wakesUpAtThree).isTrue();
    }

    @Test
    public void observerWakesUpExactlyOnceAtTheSecondCycle() {
        IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(2, 1, 0);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);
        boolean wakesUpAtTwo = awakenessStrategy.isAwake();
        awakenessStrategy.updateAwakenessState(3);
        boolean wakesUpAtThree = awakenessStrategy.isAwake();

        assertThat(wakesUpAtTwo && wakesUpAtThree).isFalse();
    }

    @Test
    public void awakenessTimeIsFixed() {
        boolean wakesUpAtTwo = false;
        boolean wakesUpAtThree = false;

        for (int i = 0; i < 1000; i++) {
            IAwakenessStrategy awakenessStrategy = new FixedAwakenessStrategy(2, 1, 0);
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
}
