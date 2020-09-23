package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Assertions;
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
    private Agent firstAgent;
    @Mock
    private Agent secondAgent;

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
        Mockito.when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(firstAgent);
    }

    @Test
    public void placeAnAgentOnBoardAgentShouldNotBeOnOtherLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.move()).thenReturn(oneOnOneCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
    }

    @Test
    public void placeTwoAgentsOnDifferentLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);
        Mockito.when(secondAgent.move()).thenReturn(oneOnOneCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);
        board.placeAgent(secondAgent.move(), secondAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(firstAgent);
        assertThat(board.getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(secondAgent);
    }

    @Test
    public void placeTwoAgentsOnSameLocation() {
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);
        board.placeAgent(secondAgent.move(), secondAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void placeNullOnBoardThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.move(), null);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardNegativeRowThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.move()).thenReturn(negativeRowCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.move(), firstAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardNegativeColThrowsException() {
        Board board = new Board(2, 2);
        Mockito.when(firstAgent.move()).thenReturn(negativeColCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.move(), firstAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardTooHighRowThrowsException() {
        Board board = new Board(2, 2);
        Location rowTooHighCoordinate = new Location(2, 0);
        Mockito.when(firstAgent.move()).thenReturn(rowTooHighCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.move(), firstAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardTooHighColThrowsException() {
        Board board = new Board(2, 2);
        Location colTooHighCoordinate = new Location(0, 2);
        Mockito.when(firstAgent.move()).thenReturn(colTooHighCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.move(), firstAgent);
        });
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldBeOnNewLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstAgent);
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldNotBeOnOldLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
    }

    @Test
    public void moveAgentToNonEmptyLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnOneCoordinate, secondAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void moveAgentFromNonEmptyLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(secondAgent);
        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstAgent);
    }

    @Test
    public void moveAgentToItsCurrentLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(firstAgent);
    }

    @Test
    public void moveNullOnBoardThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Mockito.when(firstAgent.move()).thenReturn(oneOnZeroCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), null);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardNegativeRowThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Mockito.when(firstAgent.move()).thenReturn(negativeRowCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardNegativeColThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Mockito.when(firstAgent.move()).thenReturn(negativeColCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardTooHighRowThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Location rowTooHighCoordinate = new Location(2, 0);
        Mockito.when(firstAgent.move()).thenReturn(rowTooHighCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardTooHighColThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Location colTooHighCoordinate = new Location(0, 2);
        Mockito.when(firstAgent.move()).thenReturn(colTooHighCoordinate);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        });
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToDifferentLocations() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(oneOnOneCoordinate, secondAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondAgent.move()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        board.moveAgent(oneOnOneCoordinate, secondAgent.move(), secondAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstAgent);
        assertThat(board.getAgentsOnLocation(oneOnZeroCoordinate)).containsExactly(secondAgent);
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToSameLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(oneOnOneCoordinate, secondAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        board.moveAgent(oneOnOneCoordinate, secondAgent.move(), secondAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToSameLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondAgent);
        Mockito.when(firstAgent.move()).thenReturn(oneOnOneCoordinate);
        Mockito.when(secondAgent.move()).thenReturn(oneOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        board.moveAgent(zeroOnZeroCoordinate, secondAgent.move(), secondAgent);

        assertThat(board.getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocations() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondAgent.move()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        board.moveAgent(zeroOnZeroCoordinate, secondAgent.move(), secondAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstAgent);
        assertThat(board.getAgentsOnLocation(oneOnZeroCoordinate)).containsExactly(secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocationsSecondAgentFirst() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondAgent);
        Mockito.when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        Mockito.when(secondAgent.move()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, secondAgent.move(), secondAgent);
        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstAgent);
        assertThat(board.getAgentsOnLocation(oneOnZeroCoordinate)).containsExactly(secondAgent);
    }
}
