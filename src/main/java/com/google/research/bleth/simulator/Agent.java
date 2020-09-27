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
    Location moveTo();

    /**
     * Move the agent to its next location and update the board accordingly.
     */
    void move();

    /**
     * @return a string consisting of the Agent's type and its ID.
     */
    String getTypeAndIdAsString();
}
