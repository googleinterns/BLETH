package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class AwakenessStrategyTest {
    public abstract void observerWithFirstAwakenessTimeZeroStartsAwake();

    public abstract void observerWithFirstAwakenessTimeOneStartsAsleep();

    public abstract void observerWakesUpAtRoundTwo();

    public abstract void observerThatWakesUpAtRoundTwoSleepsAtRoundOne();

    public abstract void observerThatWakesUpAtRoundTwoForTwoRoundsIsAwakeAtRoundThree();

    public abstract void observerThatWakesUpAtRoundOneForTwoRoundsAsleepAtRoundThree();

    public abstract void observerThatWakesUpAtRoundZeroAndIsAwakeForAWholeCycleStaysAwake();

    public abstract void observerWakesUpAtTheSecondCycle();

    public abstract void observerWakesUpExactlyOnceAtTheSecondCycle();
}
