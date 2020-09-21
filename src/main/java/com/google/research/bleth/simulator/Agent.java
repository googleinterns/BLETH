package com.google.research.bleth.simulator;

/** Represent either observer and beacon. */
public interface Agent {

    /** Return the location on board which the agent is moving to. */
    Location move();
}
