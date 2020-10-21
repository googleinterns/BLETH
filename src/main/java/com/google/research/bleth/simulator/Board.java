package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A container for all the agents, representing their locations, either real or estimated. */
public abstract class Board {
    private final int rowNum;
    private final int colNum;
    private ArrayTable<Integer, Integer, ArrayList<IAgent>> matrix;

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
                matrix.set(row, col, new ArrayList<IAgent>());
            }
        }
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
     * Place an agent on board if the given location is valid.
     * @param newLocation is the location where the agent will be placed.
     * @param agent is the Agent which placed on the board.
     */
    public void placeAgent(Location newLocation, IAgent agent) {
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
    public void moveAgent(Location oldLocation, Location newLocation, IAgent agent) {
        checkNotNull(agent);
        validateLocation(newLocation);
        validateLocation(oldLocation);
        matrix.get(oldLocation.row, oldLocation.col).remove(agent);
        placeAgent(newLocation, agent);
    }

    /** Returns the number of rows in the board. */
    public int getRowNum() {
        return rowNum;
    }

    /** Returns the number of columns in the board. */
    public int getColNum() {
        return colNum;
    }

    /** Returns a map that maps to each populated location the agents on this location. */
    public Multimap<Location, IAgent> agentsOnBoard() {
        Multimap<Location, IAgent> locationsToAgents = ArrayListMultimap.create();
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                locationsToAgents.putAll(new Location(row, col), matrix.get(row, col));
            }
        }
        return ImmutableListMultimap.copyOf(locationsToAgents);
    }

    /** Returns the type of the board, either real or estimated. */
    public abstract String getType();
}
