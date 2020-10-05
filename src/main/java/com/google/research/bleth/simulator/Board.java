package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A container for all the agents, representing their locations, either real or estimated. */
public class Board {
    private final int rowNum;
    private final int colNum;
    private ArrayTable<Integer, Integer, ArrayList<Agent>> matrix;

    /**
     * Create an empty board for storing agents' locations.
     * A board can represent either the real agents' locations in the simulation,
     * or the estimated locations from the resolver's point of view.
     * @param rows is number of rows.
     * @param cols is number of columns.
     */
    public Board(int rows, int cols) {
        rowNum = rows;
        colNum = cols;
        matrix = ArrayTable.create(IntStream.range(0, rows).boxed().collect(Collectors.toList()),
                                   IntStream.range(0, cols).boxed().collect(Collectors.toList()));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                matrix.set(row, col, new ArrayList<Agent>());
            }
        }
    }

    /**
     * @return number of rows.
     */
    public int getRowNum() {
        return rowNum;
    }

    /**
     * @return number of columns.
     */
    public int getColNum() {
        return colNum;
    }

    /**
     * Check if a location is within the board boundaries.
     * @param location is the location to check if valid.
     * @throws NullPointerException if the location is null.
     * @throws IllegalArgumentException if the location is without the board boundaries.
     */
    public void validateLocation(Location location) {
        checkNotNull(location);
        checkArgument(0 <= location.row && location.row < rowNum, "Invalid Location");
        checkArgument(0 <= location.col && location.col < colNum, "Invalid Location");
    }

    /**
     * Check if a location is within the board boundaries.
     * @param location is the location to check if valid.
     * @return true if the location is valid, false otherwise.
     */
    public boolean isLocationValid(Location location) {
        try {
            validateLocation(location);
        } catch (IllegalArgumentException invalidLocation) {
            return false;
        }
        return true;
    }

    /**
     * Create and return an immutable list of all the agents which located on specific location.
     * @param location is the requested location to retrieve all the agents that located on it.
     * @return an immutable copy of the list of all agents which located on location.
     */
    public List<Agent> getAgentsOnLocation(Location location) {
        validateLocation(location);
        return ImmutableList.copyOf(matrix.get(location.row, location.col));
    }

    /**
     * Place an agent on board if the given location is valid.
     * @param newLocation is the location where the agent will be placed.
     * @param agent is the Agent which placed on the board.
     */
    public void placeAgent(Location newLocation, Agent agent) {
        checkNotNull(agent);
        validateLocation(newLocation);
        matrix.get(newLocation.row, newLocation.col).add(agent);
    }

    /**
     * Remove an agent from its current location on board and place it on new location.
     * @param oldLocation is the agent's current location.
     * @param newLocation is the location where the agent will be placed.
     * @param agent is the Agent which moves from oldLocation to newLocation.
     */
    public void moveAgent(Location oldLocation, Location newLocation, Agent agent) {
        checkNotNull(agent);
        validateLocation(newLocation);
        validateLocation(oldLocation);
        matrix.get(oldLocation.row, oldLocation.col).remove(agent);
        placeAgent(newLocation, agent);
    }
}
