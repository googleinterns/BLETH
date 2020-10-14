package com.google.research.bleth.simulator;

/** Represent the awakeness strategy of an observer. */
public interface IAwakenessStrategy {

    /**
     * Activate the observer if the current round is the start of its current awakeness time
     * and turn it off if it's the end of its current awakeness time.
     * The function is called for every round between 1 and the last round without skipping.
     * @param currentRound is the the current round of the simulation.
     */
    void updateAwakenessState(int currentRound);

    /**
     * Returns true if the observer is activated, false otherwise.
     */
    boolean isAwake();
}
