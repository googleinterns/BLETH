package com.google.research.bleth.services;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Multimap;
import com.google.research.bleth.exceptions.BoardStateAlreadyExistsException;
import com.google.research.bleth.exceptions.ExceedingRoundException;
import com.google.research.bleth.simulator.AbstractSimulation;
import com.google.research.bleth.simulator.AwakenessStrategyFactory;
import com.google.research.bleth.simulator.Beacon;
import com.google.research.bleth.simulator.Board;
import com.google.research.bleth.simulator.EstimatedBoard;
import com.google.research.bleth.simulator.IAgent;
import com.google.research.bleth.simulator.Location;
import com.google.research.bleth.simulator.Observer;
import com.google.research.bleth.simulator.RandomMovementStrategy;
import com.google.research.bleth.simulator.RealBoard;
import com.google.research.bleth.simulator.StationaryMovementStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RunWith(MockitoJUnitRunner.class)
public class DatabaseServiceTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SCATTERED));

    private static final Gson GSON = new Gson();
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final int BOARD_DIMENSION = 2;
    private static final int ZERO_ROUND = 0;
    private static final int FIRST_ROUND = 1;
    private static final int MAX_NUMBER_OF_ROUNDS = 100;

    @Mock
    AbstractSimulation.Builder firstSimulationBuilder;

    @Mock
    AbstractSimulation.Builder secondSimulationBuilder;

    @Mock
    Beacon beacon;

    @Mock
    Observer observer;

    @Before
    public void setUp() {
        helper.setUp();
    }

    @Before
    public void mockFirstSimulationBuilder() {
        Mockito.when(firstSimulationBuilder.getSimulationType()).thenReturn("Stalking");
        Mockito.when(firstSimulationBuilder.getMaxNumberOfRounds()).thenReturn(100);
        Mockito.when(firstSimulationBuilder.getBeaconsNum()).thenReturn(2);
        Mockito.when(firstSimulationBuilder.getObserversNum()).thenReturn(1);
        Mockito.when(firstSimulationBuilder.getRowNum()).thenReturn(2);
        Mockito.when(firstSimulationBuilder.getColNum()).thenReturn(2);
        Mockito.when(firstSimulationBuilder.getBeaconMovementStrategy()).thenReturn(new RandomMovementStrategy());
        Mockito.when(firstSimulationBuilder.getObserverMovementStrategy()).thenReturn(new StationaryMovementStrategy());
        Mockito.when(firstSimulationBuilder.getAwakenessStrategyType()).thenReturn(AwakenessStrategyFactory.Type.FIXED);
        Mockito.when(firstSimulationBuilder.getRadius()).thenReturn(3.5);
        Mockito.when(firstSimulationBuilder.getAwakenessCycle()).thenReturn(5);
        Mockito.when(firstSimulationBuilder.getAwakenessDuration()).thenReturn(1);
    }

    @Before
    public void mockSecondSimulationBuilder() {
        Mockito.when(secondSimulationBuilder.getSimulationType()).thenReturn("Stalking");
        Mockito.when(secondSimulationBuilder.getMaxNumberOfRounds()).thenReturn(100);
        Mockito.when(secondSimulationBuilder.getBeaconsNum()).thenReturn(2);
        Mockito.when(secondSimulationBuilder.getObserversNum()).thenReturn(1);
        Mockito.when(secondSimulationBuilder.getRowNum()).thenReturn(2);
        Mockito.when(secondSimulationBuilder.getColNum()).thenReturn(2);
        Mockito.when(secondSimulationBuilder.getBeaconMovementStrategy()).thenReturn(new RandomMovementStrategy());
        Mockito.when(secondSimulationBuilder.getObserverMovementStrategy()).thenReturn(new StationaryMovementStrategy());
        Mockito.when(secondSimulationBuilder.getAwakenessStrategyType()).thenReturn(AwakenessStrategyFactory.Type.FIXED);
        Mockito.when(secondSimulationBuilder.getRadius()).thenReturn(3.5);
        Mockito.when(secondSimulationBuilder.getAwakenessCycle()).thenReturn(5);
        Mockito.when(secondSimulationBuilder.getAwakenessDuration()).thenReturn(1);
    }

    @Before
    public void mockAgents() {
        Mockito.when(beacon.getId()).thenReturn(0);
        Mockito.when(beacon.getType()).thenReturn("Beacon");
        Mockito.when(observer.getId()).thenReturn(0);
        Mockito.when(observer.getType()).thenReturn("Observer");
    }

    @Test
    public void writeNonEmptyRealBoardThenReadRealBoard_shouldGetExpectedJson() {
        DatabaseService db = DatabaseService.getInstance();
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = db.writeMetadata(firstSimulationBuilder);
        String expectedBoardState = GSON.toJson(toStaticBoardState(realBoard));

        db.writeBoardState(firstSimulationId, ZERO_ROUND, realBoard);
        String boardState = db.readRealBoardState(firstSimulationId, ZERO_ROUND);
        assertThat(boardState).isEqualTo(expectedBoardState);
    }

    @Test
    public void writeNonEmptyEstimatedBoardThenReadEstimatedBoard_shouldGetExpectedJson() {
        DatabaseService db = DatabaseService.getInstance();
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        estimatedBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        estimatedBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = db.writeMetadata(firstSimulationBuilder);
        String expectedBoardState = GSON.toJson(toStaticBoardState(estimatedBoard));

        db.writeBoardState(firstSimulationId, ZERO_ROUND, estimatedBoard);
        String boardState = db.readEstimatedBoardState(firstSimulationId, ZERO_ROUND);
        assertThat(boardState).isEqualTo(expectedBoardState);
    }

    @Test
    public void writeOnlyRealBoardThenReadEstimatedBoard_shouldGetEmptyBoard() {
        DatabaseService db = DatabaseService.getInstance();
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = db.writeMetadata(firstSimulationBuilder);
        String emptyBoardStateJson = GSON.toJson(createEmptyTable(BOARD_DIMENSION, BOARD_DIMENSION));

        db.writeBoardState(firstSimulationId, ZERO_ROUND, realBoard);

        assertThat(db.readEstimatedBoardState(firstSimulationId, ZERO_ROUND)).isEqualTo(emptyBoardStateJson);
    }

    @Test
    public void writeRealBoardThenReadRealBoardWithDifferentSimulationId_shouldGetEmptyBoard() {
        DatabaseService db = DatabaseService.getInstance();
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = db.writeMetadata(firstSimulationBuilder);
        String secondSimulationId = db.writeMetadata(secondSimulationBuilder);
        String emptyBoardStateJson = GSON.toJson(createEmptyTable(2, 2));

        db.writeBoardState(firstSimulationId, ZERO_ROUND, realBoard);

        assertThat(db.readRealBoardState(secondSimulationId, ZERO_ROUND)).isEqualTo(emptyBoardStateJson);
    }

    @Test
    public void writeEstimatedBoardThenReadEstimatedBoardWithDifferentExistingRound_shouldGetEmptyBoard() {
        DatabaseService db = DatabaseService.getInstance();
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        estimatedBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        estimatedBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = db.writeMetadata(firstSimulationBuilder);
        String emptyBoardStateJson = GSON.toJson(createEmptyTable(BOARD_DIMENSION, BOARD_DIMENSION));

        db.writeBoardState(firstSimulationId, ZERO_ROUND, estimatedBoard);

        assertThat(db.readRealBoardState(firstSimulationId, FIRST_ROUND)).isEqualTo(emptyBoardStateJson);
    }

    @Test
    public void writeEstimatedBoardThenReadEstimatedBoardWithDifferentNonExistingRound_shouldGetNull() {
        DatabaseService db = DatabaseService.getInstance();
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        estimatedBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        estimatedBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = db.writeMetadata(firstSimulationBuilder);

        db.writeBoardState(firstSimulationId, ZERO_ROUND, estimatedBoard);

        assertThat(db.readRealBoardState(firstSimulationId, MAX_NUMBER_OF_ROUNDS)).isNull();
    }

    @Test
    public void writeRealBoardWithNonExistingRound_shouldThrowException() {
        DatabaseService db = DatabaseService.getInstance();
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = db.writeMetadata(firstSimulationBuilder);

        assertThrows(ExceedingRoundException.class, () -> {
            db.writeBoardState(firstSimulationId, MAX_NUMBER_OF_ROUNDS, realBoard);
        });
    }

    @Test
    public void writeDuplicateSimulationIdAndRound_shouldThrowException() {
        DatabaseService db = DatabaseService.getInstance();
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = db.writeMetadata(firstSimulationBuilder);

        db.writeBoardState(firstSimulationId, ZERO_ROUND, realBoard);

        assertThrows(BoardStateAlreadyExistsException.class, () -> {
            db.writeBoardState(firstSimulationId, 0, realBoard);
        });
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private ArrayTable<Integer, Integer, ArrayList<String>> toStaticBoardState(Board board) {
        int rowNum = board.getRowNum();
        int colNum = board.getColNum();
        ArrayTable<Integer, Integer, ArrayList<String>> boardState = createEmptyTable(rowNum, colNum);
        Multimap<Location, IAgent> agentsOnBoard = board.agentsOnBoard();
        for (Location location : agentsOnBoard.keys()) {
            for (IAgent agent : agentsOnBoard.get(location)) {
                boardState.get(location.row, location.col).add(agent.getType() + agent.getId());
            }
        }

        return boardState;
    }

    private ArrayTable<Integer, Integer, ArrayList<String>> createEmptyTable(int rowNum, int colNum) {
        ArrayTable<Integer, Integer, ArrayList<String>> table;
        table = ArrayTable.create(IntStream.range(0, rowNum).boxed().collect(Collectors.toList()),
                IntStream.range(0, colNum).boxed().collect(Collectors.toList()));

        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                table.set(row, col, new ArrayList<>());
            }
        }

        return table;
    }
}
