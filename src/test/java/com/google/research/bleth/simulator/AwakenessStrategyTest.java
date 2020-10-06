package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class AwakenessStrategyTest {
    @Test
    public void observerWithFirstAwakenessTimeZeroStartsAwake() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(5, 3, 0);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerWithFirstAwakenessTimeOneStartsAsleep() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(5, 3, 1);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerWakesUpAtRoundTwo() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(5,3 ,2);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerThatWakesUpAtRoundTwoSleepsAtRoundOne() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(5, 3, 2);

        awakenessStrategy.updateAwakenessState(1);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerThatWakesUpAtRoundTwoForTwoRoundsIsAwakeAtRoundThree() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(5,2, 2);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        awakenessStrategy.updateAwakenessState(3);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerThatWakesUpAtRoundOneForTwoRoundsAsleepAtRoundThree() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(10, 2, 1);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        awakenessStrategy.updateAwakenessState(3);

        assertThat(awakenessStrategy.isAwake()).isFalse();
    }

    @Test
    public void observerThatWakesUpAtRoundZeroAndIsAwakeForAWholeCycleStaysAwake() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(2, 2, 0);
        awakenessStrategy.updateAwakenessState(1);
        awakenessStrategy.updateAwakenessState(2);

        assertThat(awakenessStrategy.isAwake()).isTrue();
    }

    @Test
    public void observerWakesUpAtTheSecondCycle() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(2, 1, 0);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);
        boolean wakesUpAtTwo = awakenessStrategy.isAwake();
        awakenessStrategy.updateAwakenessState(3);
        boolean wakesUpAtThree = awakenessStrategy.isAwake();

        assertThat(wakesUpAtTwo || wakesUpAtThree).isTrue();
    }

    @Test
    public void observerWakesUpExactlyOnceAtTheSecondCycle() {
        IAwakenessStrategy awakenessStrategy = createAwakenessStrategy(2, 1, 0);
        awakenessStrategy.updateAwakenessState(1);

        awakenessStrategy.updateAwakenessState(2);
        boolean wakesUpAtTwo = awakenessStrategy.isAwake();
        awakenessStrategy.updateAwakenessState(3);
        boolean wakesUpAtThree = awakenessStrategy.isAwake();

        assertThat(wakesUpAtTwo && wakesUpAtThree).isFalse();
    }

    abstract IAwakenessStrategy createAwakenessStrategy(int awakenessCycleDuration, int awakenessDuration, int firstAwakenessTime);
}
