package com.google.research.bleth.simulator;

/** A fixed awakeness strategy for an observer - the observer wakes up at the same difference from the start of the awakeness interval. */
public class FixedAwakenessStrategy implements IAwakenessStrategy {
    private final int awakenessCycleDuration;
    private final int awakenessDuration;
    private int nextAwakeningTime;
    private int nextAwakenessIntervalStart = 0;
    private boolean awake = false;

    /**
     * Create a new fixed awakeness strategy.
     * @param awakenessCycleDuration is the duration of each awakeness cycle. An observer can wake up once in a cycle.
     * @param awakenessDuration is the number of rounds that the observer is awake each time it wakes up.
     * @param firstAwakenessTime is the first time eht observer wakes up.
     */
    FixedAwakenessStrategy(int awakenessCycleDuration, int awakenessDuration, int firstAwakenessTime) {
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
            nextAwakeningTime += awakenessCycleDuration;
        }
        if (!awake && currentRound >= nextAwakeningTime) {
            awake = true;
        }
    }
}
