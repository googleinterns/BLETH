package com.google.research.bleth.simulator;

/** Represent a beacon. */
public interface IBeacon extends Agent {

    /**
     * @return a transmission based on the beacon's eid.
     */
    Transmission transmit();
}
