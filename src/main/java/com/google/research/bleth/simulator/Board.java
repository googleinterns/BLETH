package com.google.research.bleth.simulator;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A container for all the agents, representing their locations, either real or estimated. */
public class Board {
    private int rowNum;
    private int colNum;
    private ArrayList<Agent>[][] matrix;

    public Board(int rows, int cols) {
        rowNum = rows;
        colNum = cols;
        matrix = new ArrayList[rowNum][colNum];
        for (int row = 0; row < rows; row++) {
            Arrays.setAll(matrix[row], ArrayList::new);
        }
    }

    /**
     * Create and return an immutable list of all the agents which located on specific location.
     * @param location is the requested location to retrieve all the agents that located on it.
     * @return an immutable copy of the list of all agents which located on location.
     */
    public List<Agent> getAgentsOnLocation(Location location) {
        return ImmutableList.copyOf(matrix[location.row][location.col]);
    }

    /**
     * Place an agent on board if the given location is valid.
     * @param newLocation is the location where the agent will be placed.
     * @param agent is the Agent which placed on the board.
     */
    public void placeAgent(Location newLocation, Agent agent) {
        boolean isRowValid = newLocation != null && 0 <= newLocation.row && newLocation.row < rowNum;
        boolean isColValid = newLocation != null && 0 <= newLocation.col && newLocation.col < colNum;
        if (!(isRowValid && isColValid)) {
            throw new IllegalArgumentException("Invalid Location");
        }
        if (agent == null) {
            throw new IllegalArgumentException("Invalid Agent");
        }
        matrix[newLocation.row][newLocation.col].add(agent);
    }

    /**
     * Remove an agent from its current location on board and place it on new location.
     * @param oldLocation is the agent's current location.
     * @param newLocation is the location where the agent will be placed.
     * @param agent is the Agent which moves from oldLocation to newLocation.
     */
    public void moveAgent(Location oldLocation, Location newLocation, Agent agent) {
        boolean isOldRowValid = oldLocation != null && 0 <= oldLocation.row && oldLocation.row < rowNum;
        boolean isOldColValid = oldLocation != null && 0 <= oldLocation.col && oldLocation.col < colNum;
        boolean isNewRowValid = newLocation != null && 0 <= newLocation.row && newLocation.row < rowNum;
        boolean isNewColValid = newLocation != null && 0 <= newLocation.col && newLocation.col < colNum;
        if (!(isOldRowValid && isOldColValid && isNewRowValid && isNewColValid)) {
            throw new IllegalArgumentException("Invalid Location");
        }
        if (agent == null) {
            throw new IllegalArgumentException("Invalid Agent");
        }
        matrix[oldLocation.row][oldLocation.col].remove(agent);
        placeAgent(newLocation, agent);
    }
}
