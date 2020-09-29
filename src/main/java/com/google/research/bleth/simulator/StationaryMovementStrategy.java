package com.google.research.bleth.simulator;

/** A stationary movement strategy for an agent. */
public class StationaryMovementStrategy implements MovementStrategy {

    /**
     * Determine and return the new location of a static agent, which is its current location.
     * @param board is the board that the agent is placed on.
     * @param currentLocation is the current location of the agent on the board.
     * @return the location on the board that the agent is moving to.
     */
    @Override
    public Location moveTo(Board board, Location currentLocation) {
        return currentLocation;
    }
}