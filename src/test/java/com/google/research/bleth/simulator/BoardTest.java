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
    private static final Location negativeRowCoordinate = new Location(-1, 0);
    private static final Location negativeColCoordinate = new Location(0, -1);
    private static final Location zeroOnZeroCoordinate = new Location(0, 0);
    private static final Location oneOnOneCoordinate = new Location(1, 1);
    private static final Location zeroOnOneCoordinate = new Location(0, 1);
    private static final Location oneOnZeroCoordinate = new Location(1, 0);

    @Mock
    private IAgent firstIAgent;
    @Mock
    private IAgent secondIAgent;

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

    @Test
    public void newBoardIsEmpty() {
        Board board = new Board(2, 2);

        assertThat(isBoardEmpty(board, 2, 2)).isTrue();
    }

    @Test
    public void placeAnAgentOnBoardAgentShouldBeOnRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);

        board.placeAgent(firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(firstIAgent);
    }

    @Test
    public void placeAnAgentOnBoardAgentShouldNotBeOnOtherLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(oneOnOneCoordinate);

        board.placeAgent(firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
    }

    @Test
    public void placeTwoAgentsOnDifferentLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);
        Mockito.when(secondIAgent.moveTo()).thenReturn(oneOnOneCoordinate);

        board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        board.placeAgent(secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(firstIAgent);
        assertThat(board.getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(secondIAgent);
    }

    @Test
    public void placeTwoAgentsOnSameLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);

        board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        board.placeAgent(secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstIAgent, secondIAgent);
    }

    @Test
    public void placeNullOnBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);

        assertThrows(NullPointerException.class, () -> {
            board.placeAgent(firstIAgent.moveTo(), null);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardNegativeRowThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(negativeRowCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardNegativeColThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(negativeColCoordinate);

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
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstIAgent);
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldNotBeOnOldLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
    }

    @Test
    public void moveAgentToNonEmptyLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        board.placeAgent(zeroOnOneCoordinate, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstIAgent, secondIAgent);
    }

    @Test
    public void moveAgentFromNonEmptyLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(secondIAgent);
        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstIAgent);
    }

    @Test
    public void moveAgentToItsCurrentLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(firstIAgent);
    }

    @Test
    public void moveNullOnBoardThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(oneOnZeroCoordinate);

        assertThrows(NullPointerException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), null);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardNegativeRowThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(negativeRowCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardNegativeColThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(negativeColCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardTooHighRowThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        Location rowTooHighCoordinate = new Location(2, 0);
        Mockito.when(firstIAgent.moveTo()).thenReturn(rowTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardTooHighColThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        Location colTooHighCoordinate = new Location(0, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(colTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromNegativeRowOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(negativeRowCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromNegativeColOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(negativeColCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromTooHighRowOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);
        Location rowTooHighCoordinate = new Location(2, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(rowTooHighCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromTooHighColOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnZeroCoordinate);
        Location colTooHighCoordinate = new Location(0, 2);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(colTooHighCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveAnAgentFromOutsideTheBoardToOutsideTheBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstIAgent.moveTo()).thenReturn(negativeColCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(negativeRowCoordinate, firstIAgent.moveTo(), firstIAgent);
        });
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToDifferentLocations() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        board.placeAgent(oneOnOneCoordinate, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondIAgent.moveTo()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);
        board.moveAgent(oneOnOneCoordinate, secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstIAgent);
        assertThat(board.getAgentsOnLocation(oneOnZeroCoordinate)).containsExactly(secondIAgent);
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToSameLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        board.placeAgent(oneOnOneCoordinate, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);
        board.moveAgent(oneOnOneCoordinate, secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstIAgent, secondIAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToSameLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(oneOnOneCoordinate);
        Mockito.when(secondIAgent.moveTo()).thenReturn(oneOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);
        board.moveAgent(zeroOnZeroCoordinate, secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(firstIAgent, secondIAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocations() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondIAgent.moveTo()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);
        board.moveAgent(zeroOnZeroCoordinate, secondIAgent.moveTo(), secondIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstIAgent);
        assertThat(board.getAgentsOnLocation(oneOnZeroCoordinate)).containsExactly(secondIAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocationsSecondAgentFirst() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstIAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondIAgent);
        Mockito.when(firstIAgent.moveTo()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondIAgent.moveTo()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, secondIAgent.moveTo(), secondIAgent);
        board.moveAgent(zeroOnZeroCoordinate, firstIAgent.moveTo(), firstIAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstIAgent);
        assertThat(board.getAgentsOnLocation(oneOnZeroCoordinate)).containsExactly(secondIAgent);
    }
}
