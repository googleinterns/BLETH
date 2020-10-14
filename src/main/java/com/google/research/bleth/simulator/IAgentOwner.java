package com.google.research.bleth.simulator;

/** Represents an authorized owner of an agent. */
public interface IAgentOwner {

    /**
     * Update the agent's location on board.
     * @param oldLocation is the agent's current location.
     * @param newLocation is the location where the agent will be placed.
     * @param agent is the Agent which moves from oldLocation to newLocation.
     */
    void updateAgentLocation(Location oldLocation, Location newLocation, IAgent agent);

    /**
     * Check if a location is within the board boundaries.
     * @param location is the location to check if valid.
     * @return true if the location is valid, false otherwise.
     */
    boolean isLocationValid(Location location);
}
