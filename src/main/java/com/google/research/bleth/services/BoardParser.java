package com.google.research.bleth.services;

import com.google.common.collect.ArrayTable;
import com.google.gson.Gson;
import com.google.research.bleth.simulator.Board;
import com.google.research.bleth.simulator.Location;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A utility class providing a method for parsing a Board object into a JSON string.
 */
public class BoardParser {

    /**
     * A private constructor for preventing object creation.
     */
    private BoardParser() { }

    /**
     * Parse a Board object into a JSON string.
     * @param board a Board object.
     * @return a JSON string.
     */
    public static String parse(Board board) {
        // Create new table where each cell is a list of strings.
        ArrayTable<Integer, Integer, ArrayList<String>> boardState =
                ArrayTable.create(IntStream.range(0, board.getRowNum()).boxed().collect(Collectors.toList()),
                        IntStream.range(0, board.getColNum()).boxed().collect(Collectors.toList()));

        // Fill each boardState cell with the list of the corresponding agents string ids.
        for (int row = 0; row < board.getRowNum(); row++) {
            for (int col = 0; col < board.getColNum(); col++) {
                ArrayList<String> agentsIdList =
                        board.getAgentsOnLocation(new Location(row, col)).stream()
                                .map((agent) -> agent.getType() + agent.getId())
                                .collect(Collectors.toCollection(ArrayList::new));
                boardState.set(row, col, agentsIdList);
            }
        }

        // Deserialize boardState into a JSON string and return.
        Gson gson = new Gson();
        return gson.toJson(boardState);
    }
}
