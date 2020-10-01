package com.google.research.bleth.simulator;

/** Represent the awakeness strategy of an observer. */
public interface AwakenessStrategy {

    /**
     * Determine when the observer wakes up next.
     * @param nextIntervalStart is the first round in which the observer can wake up again.
     * @param awakenessCycle is the duration of each awakeness cycle - an observer can wake up once such cycle.
     * @param awakenessDuration is the number of rounds that the observer is awake each time it wakes up.
     * @param lastAwakeningTime is the last round the observer woke up.
     * @return the next round in which the observer wakes up.
     */
    int nextTime(int nextIntervalStart, int awakenessCycle, int awakenessDuration, int lastAwakeningTime);
}
