package com.google.research.bleth.simulator;

import com.google.auto.value.AutoValue;

/** An advertisement of a beacon. */
@AutoValue
public abstract class Transmission {
    public static Transmission create(int advertisement) {
        return new AutoValue_Transmission(advertisement);
    }

    public abstract int advertisement();
}