package com.google.research.bleth.simulator;

/** Represent both observer and beacon. */
public interface Agent {

    /**
     * @return the current agent's location on the real board.
     */
    Location getLocation();

    /**
     * Calculate the location the agent is moving to, based on its current location and its strategy.
     * @return the location on board which the agent is moving to.
     */
    Location move();
}
