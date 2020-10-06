package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public final class BoardTest{
    private static final Location NEGATIVE_ROW_COORDINATE = new Location(-1, 0);
    private static final Location NEGATIVE_COL_COORDINATE = new Location(0, -1);
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = new Location(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = new Location(1, 0);

    @Mock
    private IAgent firstIAgent;
    @Mock
    private IAgent secondIAgent;

    @Test
    public void newBoardIsEmpty() {
        Board board = new Board(2, 2);

        assertThat(isBoardEmpty(board, 2, 2)).isTrue();
    }

    @Test
    public void placeAnAgentOnBoardAgentShouldBeOnRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        board.placeAgent(firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).containsExactly(firstIAgent);
    }

    @Test
    public void placeAnAgentOnBoardAgentShouldNotBeOnOtherLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ONE_ON_ONE_COORDINATE);

        board.placeAgent(firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).isEmpty();
    }

    @Test
    public void placeTwoAgentsOnDifferentLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(secondIAgent.moveTo()).thenReturn(ONE_ON_ONE_COORDINATE);

        board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        board.placeAgent(secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).containsExactly(firstIAgent);
        assertThat(board.getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).containsExactly(secondIAgent);
    }

    @Test
    public void placeTwoAgentsOnSameLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        board.placeAgent(secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstIAgent, secondIAgent);
    }

    @Test
    public void placeNullOnBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        assertThrows(NullPointerException.class, () -> {
            board.placeAgent(firstIAgent.moveTo(), null);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardNegativeRowThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(NEGATIVE_ROW_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardNegativeColThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(NEGATIVE_COL_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardTooHighRowThrowsException() {
        Board board = new Board(2, 2);
        Location rowTooHighCoordinate = new Location(2, 0);
        Mockito.when(firstIAgent.moveTo()).thenReturn(rowTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardTooHighColThrowsException() {
        Board board = new Board(2, 2);
        Location colTooHighCoordinate = new Location(0, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(colTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldBeOnNewLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstIAgent);
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldNotBeOnOldLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).isEmpty();
    }

    @Test
    public void moveAgentToNonEmptyLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        board.placeAgent(ZERO_ON_ONE_COORDINATE, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstIAgent, secondIAgent);
    }

    @Test
    public void moveAgentFromNonEmptyLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).containsExactly(secondIAgent);
        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstIAgent);
    }

    @Test
    public void moveAgentToItsCurrentLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).containsExactly(firstIAgent);
    }

    @Test
    public void moveNullOnBoardThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ONE_ON_ZERO_COORDINATE);

        assertThrows(NullPointerException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), null);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardNegativeRowThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(NEGATIVE_ROW_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardNegativeColThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(NEGATIVE_COL_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardTooHighRowThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        Location rowTooHighCoordinate = new Location(2, 0);
        Mockito.when(firstIAgent.moveTo()).thenReturn(rowTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardTooHighColThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        Location colTooHighCoordinate = new Location(0, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(colTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromNegativeRowOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(NEGATIVE_ROW_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromNegativeColOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(NEGATIVE_COL_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromTooHighRowOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);
        Location rowTooHighCoordinate = new Location(2, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(rowTooHighCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromTooHighColOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);
        Location colTooHighCoordinate = new Location(0, 2);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(colTooHighCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromOutsideTheBoardToOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(NEGATIVE_COL_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(NEGATIVE_ROW_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToDifferentLocations() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        board.placeAgent(ONE_ON_ONE_COORDINATE, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondIAgent.moveTo()).thenReturn(ONE_ON_ZERO_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        board.moveAgent(ONE_ON_ONE_COORDINATE, secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstIAgent);
        assertThat(board.getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(secondIAgent);
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToSameLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        board.placeAgent(ONE_ON_ONE_COORDINATE, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        board.moveAgent(ONE_ON_ONE_COORDINATE, secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstIAgent, secondIAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToSameLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ONE_ON_ONE_COORDINATE);
        Mockito.when(secondIAgent.moveTo()).thenReturn(ONE_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        board.moveAgent(ZERO_ON_ZERO_COORDINATE, secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).containsExactly(firstIAgent, secondIAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocations() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondIAgent.moveTo()).thenReturn(ONE_ON_ZERO_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);
        board.moveAgent(ZERO_ON_ZERO_COORDINATE, secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstIAgent);
        assertThat(board.getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(secondIAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocationsSecondAgentFirst() {
        Board board = new Board(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondIAgent.moveTo()).thenReturn(ONE_ON_ZERO_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, secondIAgent.moveTo(), secondIAgent);
        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstIAgent);
        assertThat(board.getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(secondIAgent);
    }

    private boolean isBoardEmpty(Board board, int rowNum, int colNum) {
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                if (!board.getAgentsOnLocation(new Location(row, col)).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
