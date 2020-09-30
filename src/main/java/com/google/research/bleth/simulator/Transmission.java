package com.google.research.bleth.simulator;

/** An advertisement of a beacon. */
public class Transmission {
    final int advertisement;

    /**
     * @param eid the ephemeral ID of the beacon.
     */
    Transmission(int eid) {
        advertisement = eid;
    }
}
