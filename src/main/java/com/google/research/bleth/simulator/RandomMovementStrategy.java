package com.google.research.bleth.simulator;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** A random movement strategy for an agent. */
public class RandomMovementStrategy implements MovementStrategy {

    private final static List<List<Integer>> directions = ImmutableList.of(ImmutableList.of(0, -1), ImmutableList.of(0, 1),
                                                                     ImmutableList.of(-1, 0), ImmutableList.of(1, 0));

    /**
     * Return the new location of a randomly moving agent, which is walking a single step in one of its possible
     * directions from its current location. If the agent can't move to any direction - return its current location.
     * @param board is the board that the agent is placed on.
     * @param currentLocation is the current location of the agent on the board.
     * @return the location on the board that the agent is moving to.
     */
    @Override
    public Location move(Board board, Location currentLocation) {
        List<List<Integer>> shuffledDirections = new ArrayList<>(directions);
        Collections.shuffle(shuffledDirections);
        for (List<Integer> direction : shuffledDirections) {
            Location newLocation = new Location(currentLocation.row + direction.get(0),
                                                currentLocation.col + direction.get(1));
            if (!board.isLocationInvalid(newLocation)) {
                return newLocation;
            }
        }
        return currentLocation;
    }
}
