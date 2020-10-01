package com.google.research.bleth.simulator;

/** Represent an observer. */
public interface IObserver extends Agent {

    /**
     * Add a transmission to the transmissions the observer observed so far in the current round.
     * @param beaconTransmission the new transmission the observer observe.
     */
    void observe(Transmission beaconTransmission);

    /**
     * Deliver all the transmissions the observer observed in the current round to its resolver.
     */
    void passInformationToResolver();

    /** Returns true if the observer is active, false otherwise. */
    boolean isAwake();
}
