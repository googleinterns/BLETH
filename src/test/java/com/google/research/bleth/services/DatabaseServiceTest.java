package com.google.research.bleth.services;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.research.bleth.simulator.Agent;
import com.google.research.bleth.simulator.Board;
import com.google.research.bleth.simulator.Location;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RunWith(MockitoJUnitRunner.class)
public final class DatabaseServiceTest extends TestCase {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SEQUENTIAL));

    private static final String firstSimulationId = "test-tracing-sim-id-1";
    private static final String secondSimulationId = "test-tracing-sim-id-2";
    private static final int roundOne = 1;
    private static final int roundTwo = 2;

    private static final Location zeroOnZeroCoordinate = new Location(0, 0);
    private static final Location oneOnOneCoordinate = new Location(1, 1);
    private static final Location zeroOnOneCoordinate = new Location(0, 1);
    private static final Location oneOnZeroCoordinate = new Location(1, 0);

    @Mock
    private Agent firstAgent;
    @Mock
    private Agent secondAgent;

    @Before
    public void setUp() {
        helper.setUp();
    }

    @Test
    public void writeAndThenReadEmptyRealBoardState_ShouldGetEqualBoardState() {

        DatabaseService db = DatabaseService.getInstance();
        Board emptyRealBoardState = new Board(2, 2);

        db.writeRealBoardState(firstSimulationId, roundOne, emptyRealBoardState);
        String outputRealBoardState = db.getRealBoardState(firstSimulationId, roundOne);

        assertThat(BoardParser.parse(emptyRealBoardState)).isEqualTo(outputRealBoardState);
    }

    @Test
    public void writeAndThenReadNonRealBoardState_ShouldGetEqualBoardState() {

        DatabaseService db = DatabaseService.getInstance();
        Board nonEmptyRealBoard = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);
        nonEmptyRealBoard.placeAgent(firstAgent.moveTo(), firstAgent);
        String nonEmptyRealBoardState = BoardParser.parse(nonEmptyRealBoard);

        db.writeRealBoardState(firstSimulationId, roundOne, nonEmptyRealBoard);
        String outputRealBoardState = db.getRealBoardState(firstSimulationId, roundOne);

        assertThat(nonEmptyRealBoardState).isEqualTo(outputRealBoardState);
    }

    @Test
    public void writeThenDeleteThenReadEmptyRealBoardState_ShouldGetNull () {

        DatabaseService db = DatabaseService.getInstance();
        Board emptyRealBoardState = new Board(2, 2);

        db.writeRealBoardState(firstSimulationId, roundOne, emptyRealBoardState);
        db.deleteAllSimulationRealBoardStates(firstSimulationId);

        assertNull(db.getRealBoardState(firstSimulationId, roundOne));
    }

    @Test
    public void readNonExistingRealBoardState_ShouldGetNull() {

        DatabaseService db = DatabaseService.getInstance();

        String nonExistingRealBoardState = db.getRealBoardState(firstSimulationId, roundOne);

        assertNull(nonExistingRealBoardState);
    }

    @Test
    public void writeAndReadSameBoardStateAsRealAndEstimated_ShouldGetEqualBoardStates() {

        DatabaseService db = DatabaseService.getInstance();
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);
        Mockito.when(secondAgent.moveTo()).thenReturn(oneOnZeroCoordinate);
        board.placeAgent(firstAgent.moveTo(), firstAgent);
        board.placeAgent(secondAgent.moveTo(), secondAgent);

        db.writeRealBoardState(firstSimulationId, roundOne, board);
        db.writeEstimatedBoardState(firstSimulationId, roundOne, board);
        String outputRealBoardState = db.getRealBoardState(firstSimulationId, roundOne);
        String outputEstimatedBoardState = db.getEstimatedBoardState(firstSimulationId, roundOne);

        assertThat(outputRealBoardState).isEqualTo(outputEstimatedBoardState);
    }

    @Test
    public void writeRealBoardSameSimulationIdDifferentRoundThenReadBoth_ShouldGetEqualBoardStates() {

        DatabaseService db = DatabaseService.getInstance();
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(oneOnOneCoordinate);
        Mockito.when(secondAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        board.placeAgent(firstAgent.moveTo(), firstAgent);
        board.placeAgent(secondAgent.moveTo(), secondAgent);

        db.writeRealBoardState(firstSimulationId, roundOne, board);
        db.writeRealBoardState(firstSimulationId, roundTwo, board);
        String outputFirstRealBoardState = db.getRealBoardState(firstSimulationId, roundOne);
        String outputSecondRealBoardState = db.getRealBoardState(firstSimulationId, roundTwo);

        assertThat(outputFirstRealBoardState).isEqualTo(outputSecondRealBoardState);
    }

    @Test
    public void writeRealBoardSameRoundDifferentSimulationIdThenReadBoth_ShouldGetEqualBoardStates() {

        DatabaseService db = DatabaseService.getInstance();
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(oneOnOneCoordinate);
        Mockito.when(secondAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        board.placeAgent(firstAgent.moveTo(), firstAgent);
        board.placeAgent(secondAgent.moveTo(), secondAgent);

        db.writeRealBoardState(firstSimulationId, roundOne, board);
        db.writeRealBoardState(secondSimulationId, roundOne, board);
        String outputFirstRealBoardState = db.getRealBoardState(firstSimulationId, roundOne);
        String outputSecondRealBoardState = db.getRealBoardState(secondSimulationId, roundOne);

        assertThat(outputFirstRealBoardState).isEqualTo(outputSecondRealBoardState);
    }

    @Test
    public void writeRealAndEstimatedBoardStatesThenDeleteAllRealBoardStatesThenReadEstimated_ShouldGetEqualToOriginal() {

        DatabaseService db = DatabaseService.getInstance();
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);
        Mockito.when(secondAgent.moveTo()).thenReturn(oneOnZeroCoordinate);
        board.placeAgent(firstAgent.moveTo(), firstAgent);
        board.placeAgent(secondAgent.moveTo(), secondAgent);
        String boardState = BoardParser.parse(board);

        db.writeRealBoardState(firstSimulationId, roundOne, board);
        db.writeEstimatedBoardState(firstSimulationId, roundOne, board);
        db.deleteAllSimulationRealBoardStates(firstSimulationId);
        String outputEstimatedBoardState = db.getEstimatedBoardState(firstSimulationId, roundOne);

        assertThat(boardState).isEqualTo(outputEstimatedBoardState);
    }

    @Test
    public void writeRealBoardStateFromSameBoardsWithSameSimulationIdAndRound_ShouldThrowException() {

        DatabaseService db = DatabaseService.getInstance();
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(oneOnOneCoordinate);
        Mockito.when(secondAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        board.placeAgent(firstAgent.moveTo(), firstAgent);
        board.placeAgent(secondAgent.moveTo(), secondAgent);

        db.writeRealBoardState(firstSimulationId, roundOne, board);

        assertThrows(PreparedQuery.TooManyResultsException.class, () -> {
            db.writeRealBoardState(firstSimulationId, roundOne, board);
        });
    }

    @Test
    public void writeRealBoardStateFromDifferentBoardsWithSameSimulationIdAndRound_ShouldThrowException() {

        DatabaseService db = DatabaseService.getInstance();
        Board firstBoard = new Board(2, 2);
        Board secondBoard = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(oneOnOneCoordinate);
        Mockito.when(secondAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        firstBoard.placeAgent(firstAgent.moveTo(), firstAgent);
        firstBoard.placeAgent(secondAgent.moveTo(), secondAgent);
        secondBoard.placeAgent(secondAgent.moveTo(), secondAgent);

        db.writeRealBoardState(firstSimulationId, roundOne, firstBoard);

        assertThrows(PreparedQuery.TooManyResultsException.class, () -> {
            db.writeRealBoardState(firstSimulationId, roundOne, secondBoard);
        });
    }

    @Test
    public void writeEstimatedBoardStateFromSameBoardsWithSameSimulationIdAndRound_ShouldThrowException() {

        DatabaseService db = DatabaseService.getInstance();
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(oneOnOneCoordinate);
        Mockito.when(secondAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        board.placeAgent(firstAgent.moveTo(), firstAgent);
        board.placeAgent(secondAgent.moveTo(), secondAgent);

        db.writeEstimatedBoardState(firstSimulationId, roundOne, board);

        assertThrows(PreparedQuery.TooManyResultsException.class, () -> {
            db.writeEstimatedBoardState(firstSimulationId, roundOne, board);
        });
    }

    @Test
    public void writeEstimatedBoardStateFromDifferentBoardsWithSameSimulationIdAndRound_ShouldThrowException() {

        DatabaseService db = DatabaseService.getInstance();
        Board firstBoard = new Board(2, 2);
        Board secondBoard = new Board(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(oneOnOneCoordinate);
        Mockito.when(secondAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        firstBoard.placeAgent(firstAgent.moveTo(), firstAgent);
        firstBoard.placeAgent(secondAgent.moveTo(), secondAgent);
        secondBoard.placeAgent(secondAgent.moveTo(), secondAgent);

        db.writeEstimatedBoardState(firstSimulationId, roundOne, firstBoard);

        assertThrows(PreparedQuery.TooManyResultsException.class, () -> {
            db.writeEstimatedBoardState(firstSimulationId, roundOne, secondBoard);
        });
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
}
