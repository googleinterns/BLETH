package com.google.research.bleth.services;

import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.common.collect.ArrayTable;
import com.google.research.bleth.simulator.Agent;
import com.google.research.bleth.simulator.Board;
import com.google.research.bleth.simulator.Location;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BoardParserTest extends TestCase {

    private static final Location zeroOnZeroCoordinate = new Location(0, 0);
    private static final Location oneOnOneCoordinate = new Location(1, 1);
    private static final Location zeroOnOneCoordinate = new Location(0, 1);
    private static final Location oneOnZeroCoordinate = new Location(1, 0);

    @Mock
    private Agent firstAgent;
    @Mock
    private Agent secondAgent;

    public ArrayTable<Integer, Integer, ArrayList<String>> emptyStringListTable() {
        ArrayTable<Integer, Integer, ArrayList<String>> table;
        table = ArrayTable.create(IntStream.range(0, 2).boxed().collect(Collectors.toList()),
                        IntStream.range(0, 2).boxed().collect(Collectors.toList()));

        for (int row = 0; row < 2; row++) {
            for (int col = 0; col < 2; col++) {
                table.set(row, col, new ArrayList<String>());
            }
        }

        return table;
    }

    public void addAgentIdInLocation (ArrayTable<Integer, Integer, ArrayList<String>> table, Agent agent, Location location) {
        table.get(location.row, location.col).add(agent.getType() + agent.getId());
    }

    @Test
    public void parseEmptyBoard() {

        // Arrange board.
        Board board = new Board(2, 2);

        // Arrange expected serialized board.
        ArrayTable<Integer, Integer, ArrayList<String>> boardState = emptyStringListTable();
        String expectedSerialized = new Gson().toJson(boardState);

        String parsed = BoardParser.parse(board);

        assertThat(expectedSerialized).isEqualTo(parsed);
    }

    @Test
    public void parseBoardWithSingleAgent() {

        // Mock agent.
        Mockito.when(firstAgent.getId()).thenReturn(1);
        Mockito.when(firstAgent.getType()).thenReturn("Beacon");

        // Arrange board.
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);

        // Arrange expected serialized board.
        ArrayTable<Integer, Integer, ArrayList<String>> boardState = emptyStringListTable();
        addAgentIdInLocation(boardState, firstAgent, zeroOnZeroCoordinate);
        String expectedSerialized = new Gson().toJson(boardState);

        String parsed = BoardParser.parse(board);

        assertThat(expectedSerialized).isEqualTo(parsed);
    }

    @Test
    public void parseEmptyBoardWithTwoAgentsInDifferentLocations() {

        // Mock agents.
        Mockito.when(firstAgent.getId()).thenReturn(1);
        Mockito.when(firstAgent.getType()).thenReturn("Beacon");
        Mockito.when(firstAgent.getId()).thenReturn(2);
        Mockito.when(firstAgent.getType()).thenReturn("Observer");

        // Arrange board.
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnOneCoordinate, firstAgent);
        board.placeAgent(oneOnZeroCoordinate, secondAgent);

        // Arrange expected serialized board.
        ArrayTable<Integer, Integer, ArrayList<String>> boardState = emptyStringListTable();
        addAgentIdInLocation(boardState, firstAgent, zeroOnOneCoordinate);
        addAgentIdInLocation(boardState, secondAgent, oneOnZeroCoordinate);
        String expectedSerialized = new Gson().toJson(boardState);

        String parsed = BoardParser.parse(board);

        assertThat(expectedSerialized).isEqualTo(parsed);
    }

    @Test
    public void parseEmptyBoardWithTwoAgentsInSameLocation() {

        // Mock agents.
        Mockito.when(firstAgent.getId()).thenReturn(1);
        Mockito.when(firstAgent.getType()).thenReturn("Beacon");
        Mockito.when(firstAgent.getId()).thenReturn(2);
        Mockito.when(firstAgent.getType()).thenReturn("Observer");

        // Arrange board.
        Board board = new Board(2, 2);
        board.placeAgent(oneOnOneCoordinate, firstAgent);
        board.placeAgent(oneOnOneCoordinate, secondAgent);

        // Arrange expected serialized board.
        ArrayTable<Integer, Integer, ArrayList<String>> boardState = emptyStringListTable();
        addAgentIdInLocation(boardState, firstAgent, oneOnOneCoordinate);
        addAgentIdInLocation(boardState, secondAgent, oneOnOneCoordinate);
        String expectedSerialized = new Gson().toJson(boardState);

        String parsed = BoardParser.parse(board);

        assertThat(expectedSerialized).isEqualTo(parsed);
    }
}
