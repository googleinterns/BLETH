package com.google.research.bleth.simulator;

import java.util.Objects;

/** An advertisement of a beacon. */
public class Transmission {
    final int advertisement;

    /**
     * @param eid the ephemeral ID of the beacon.
     */
    Transmission(int eid) {
        advertisement = eid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transmission that = (Transmission) o;
        return advertisement == that.advertisement;
    }
}
