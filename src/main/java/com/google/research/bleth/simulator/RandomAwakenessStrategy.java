package com.google.research.bleth.simulator;

import java.util.Random;

/** A random awakeness strategy for an observer - the observer wakes up at random time each awakeness cycle. */
public class RandomAwakenessStrategy implements AwakenessStrategy {
    private Random random = new Random();

    /**
     * Determine when the observer wakes up next randomly within the next awakeness cycle.
     * @param nextIntervalStart is the first round in which the observer can wake up again.
     * @param awakenessCycle is the duration of each awakeness cycle - an observer can wake up once such cycle.
     * @param awakenessDuration is the number of rounds that the observer is awake each time it wakes up.
     * @param lastAwakeningTime is the last round the observer woke up.
     * @return the next round in which the observer wakes up.
     */
    @Override
    public int nextTime(int nextIntervalStart, int awakenessCycle, int awakenessDuration, int lastAwakeningTime) {
        return nextIntervalStart + random.nextInt(awakenessCycle - awakenessDuration);
    }
}
