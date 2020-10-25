package com.google.research.bleth.simulator;

/** Represent a beacon. */
public interface IBeacon extends IAgent {

    /** Returns a transmission based on the beacon's eid. */
    Transmission transmit();
}
