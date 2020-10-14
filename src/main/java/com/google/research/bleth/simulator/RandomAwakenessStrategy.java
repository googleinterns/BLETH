package com.google.research.bleth.simulator;

import java.util.Random;

/** A random awakeness strategy for an observer - the observer wakes up at random time each awakeness cycle. */
public class RandomAwakenessStrategy implements IAwakenessStrategy {
    private final int awakenessCycleDuration;
    private final int awakenessDuration;
    private int nextAwakeningTime;
    private int nextAwakenessIntervalStart = 0;
    private boolean awake = false;
    private Random random = new Random();

    /**
     * Create a new random awakeness strategy.
     * @param awakenessCycleDuration is the duration of each awakeness cycle. An observer can wake up once in a cycle.
     * @param awakenessDuration is the number of rounds that the observer is awake each time it wakes up.
     * @param firstAwakenessTime is the first time eht observer wakes up.
     */
    RandomAwakenessStrategy(int awakenessCycleDuration, int awakenessDuration, int firstAwakenessTime) {
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
            nextAwakenessIntervalStart += awakenessCycleDuration;
            nextAwakeningTime = nextAwakenessIntervalStart + random.nextInt(awakenessCycleDuration - awakenessDuration + 1);
        }
        if (!awake && currentRound >= nextAwakeningTime) {
            awake = true;
        }
    }
}
