package com.google.research.bleth.simulator;

/** Represent the movement strategy of an agent. */
public interface IMovementStrategy {

    /**
     * Determine the next location of an agent, based on its current location on the board and its strategy.
     * @param board is the board that the agent is placed on.
     * @param currentLocation is the current location of the agent on the board.
     * @return the location on the board that the agent is moving to.
     */
    Location moveTo(Board board, Location currentLocation);
}
