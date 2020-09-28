package com.google.research.bleth.simulator;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
     * Check if a location is within the board boundaries. If not - throw an exception.
     * @param location is the location to check if valid.
     * @return true if the location is valid, false otherwise.
     */
    public void isLocationInvalid(Location location) {
        checkNotNull(location);
        checkArgument(0 <= location.row && location.row < rowNum, "Invalid Location");
        checkArgument(0 <= location.col && location.col < colNum, "Invalid Location");
    }

    /**
     * Create and return an immutable list of all the agents which located on specific location.
     * @param location is the requested location to retrieve all the agents that located on it.
     * @return an immutable copy of the list of all agents which located on location.
     */
    public List<Agent> getAgentsOnLocation(Location location) {
        isLocationInvalid(location);
        return ImmutableList.copyOf(matrix.get(location.row, location.col));
    }

    /**
     * Place an agent on board if the given location is valid.
     * @param newLocation is the location where the agent will be placed.
     * @param agent is the Agent which placed on the board.
     */
    public void placeAgent(Location newLocation, Agent agent) {
        checkNotNull(agent);
        isLocationInvalid(newLocation);
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
        isLocationInvalid(newLocation);
        isLocationInvalid(oldLocation);
        matrix.get(oldLocation.row, oldLocation.col).remove(agent);
        placeAgent(newLocation, agent);
    }

    /**
     * return a JSON string to represent the board state.
     * @return a JSON string. each matrix cell contains a list of string encoding agent type and id.
     */
    public String getState() {
        // Create new table where each cell is a list of strings.
        ArrayTable<Integer, Integer, ArrayList<String>> boardState =
                ArrayTable.create(IntStream.range(0, this.rowNum).boxed().collect(Collectors.toList()),
                                  IntStream.range(0, this.colNum).boxed().collect(Collectors.toList()));

        // Fill each boardState cell with the list of the corresponding agents string ids.
        for (int row = 0; row < this.rowNum; row++) {
            for (int col = 0; col < this.colNum; col++) {
                ArrayList<String> agentsIdList =
                        this.matrix.get(row, col).stream()
                                .map(Agent::getTypeAndIdAsString)
                                .collect(Collectors.toCollection(ArrayList::new));
                boardState.set(row, col, agentsIdList);
            }
        }

        // Deserialize boardState into a JSON string and return.
        Gson gson = new Gson();
        return gson.toJson(boardState);
    }
}
