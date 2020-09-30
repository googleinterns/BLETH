package com.google.research.bleth.simulator;

/** A fixed awakeness strategy for an observer - the observer wakes up at the same difference from the start of the awakeness interval. */
public class FixedAwakenessStrategy implements AwakenessStrategy {

    /**
     * Determine when the observer wakes up next according to the last time, at the same difference
     * from the awakeness interval's start as last time.
     * @param nextIntervalStart is the first round in which the observer can wake up again.
     * @param awakenessCycle is the duration of each awakeness cycle - an observer can wake up once such cycle.
     * @param awakenessDuration is the number of rounds that the observer is awake each time it wakes up.
     * @param lastAwakenessTime is the last round the observer woke up.
     * @return the next round in which the observer wakes up.
     */
    @Override
    public int nextTime(int nextIntervalStart, int awakenessCycle, int awakenessDuration, int lastAwakenessTime) {
        return lastAwakenessTime + awakenessCycle;
    }
}
