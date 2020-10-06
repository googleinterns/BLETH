package com.google.research.bleth.simulator;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A random movement strategy for an agent. */
public class RandomIMovementStrategy implements IMovementStrategy {

    private final static List<Direction> directions =
            ImmutableList.copyOf(Direction.values());

    /**
     * Determine and return the new location of a randomly moving agent, which is walking a single step in one of the
     * possible directions from its current location. If an agent can't move to any direction, return its current location.
     * @param board is the board that the agent is placed on.
     * @param currentLocation is the current location of the agent on the board.
     * @return the location on the board that the agent is moving to.
     */
    @Override
    public Location moveTo(Board board, Location currentLocation) {
        List<Direction> shuffledDirections = new ArrayList<>(directions);
        Collections.shuffle(shuffledDirections);
        for (Direction direction : shuffledDirections) {
            Location newLocation = currentLocation.moveInDirection(direction);
            if (board.isLocationValid(newLocation)) {
                return newLocation;
            }
        }
        return currentLocation;
    }
}
