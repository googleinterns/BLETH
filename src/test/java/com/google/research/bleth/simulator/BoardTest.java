package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import org.junit.runner.RunWith;
import org.junit.Test;
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
                if (!board.matrix[row][col].isEmpty()) {
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
        when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][0]).containsExactly(firstAgent);
    }

    @Test
    public void placeAnAgentOnBoardAgentShouldNotBeOnOtherLocation() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(oneOnOneCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][0]).isEmpty();
    }

    @Test
    public void placeTwoAgentsOnDifferentLocation() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);
        when(secondAgent.move()).thenReturn(oneOnOneCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);
        board.placeAgent(secondAgent.move(), secondAgent);

        assertThat(board.matrix[0][0]).containsExactly(firstAgent);
        assertThat(board.matrix[1][1]).containsExactly(secondAgent);
    }

    @Test
    public void placeTwoAgentsOnSameLocation() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        when(secondAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);
        board.placeAgent(secondAgent.move(), secondAgent);

        assertThat(board.matrix[0][1]).containsExactly(firstAgent, secondAgent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeNullOnBoardThrowsException() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);

        board.placeAgent(firstAgent.move(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeAnAgentOutsideTheBoardNegativeRowThrowsException() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(negativeRowCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeAnAgentOutsideTheBoardNegativeColThrowsException() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(negativeColCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeAnAgentOutsideTheBoardTooHighRowThrowsException() {
        Board board = new Board(2, 2);
        Location rowTooHighCoordinate = new Location(2, 0);
        when(firstAgent.move()).thenReturn(rowTooHighCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void placeAnAgentOutsideTheBoardTooHighColThrowsException() {
        Board board = new Board(2, 2);
        Location colTooHighCoordinate = new Location(0, 2);
        when(firstAgent.move()).thenReturn(colTooHighCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldBeOnNewLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][1]).containsExactly(firstAgent);
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldNotBeOnOldLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][0]).isEmpty();
    }

    @Test
    public void moveAgentToNonEmptyLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnOneCoordinate, secondAgent);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][1]).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void moveAgentFromNonEmptyLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondAgent);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][0]).containsExactly(secondAgent);
        assertThat(board.matrix[0][1]).containsExactly(firstAgent);
    }

    @Test
    public void moveAgentToItsCurrentLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][0]).containsExactly(firstAgent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveNullOnBoardThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        when(firstAgent.move()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveAnAgentOutsideTheBoardNegativeRowThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        when(firstAgent.move()).thenReturn(negativeRowCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveAnAgentOutsideTheBoardNegativeColThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        when(firstAgent.move()).thenReturn(negativeColCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveAnAgentOutsideTheBoardTooHighRowThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Location rowTooHighCoordinate = new Location(2, 0);
        when(firstAgent.move()).thenReturn(rowTooHighCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveAnAgentOutsideTheBoardTooHighColThrowsException() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        Location colTooHighCoordinate = new Location(0, 2);
        when(firstAgent.move()).thenReturn(colTooHighCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToDifferentLocations() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(oneOnOneCoordinate, secondAgent);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        when(secondAgent.move()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        board.moveAgent(oneOnOneCoordinate, secondAgent.move(), secondAgent);

        assertThat(board.matrix[0][1]).containsExactly(firstAgent);
        assertThat(board.matrix[1][0]).containsExactly(secondAgent);
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToSameLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(oneOnOneCoordinate, secondAgent);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        when(secondAgent.move()).thenReturn(zeroOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        board.moveAgent(oneOnOneCoordinate, secondAgent.move(), secondAgent);

        assertThat(board.matrix[0][1]).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToSameLocation() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondAgent);
        when(firstAgent.move()).thenReturn(oneOnOneCoordinate);
        when(secondAgent.move()).thenReturn(oneOnOneCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        board.moveAgent(zeroOnZeroCoordinate, secondAgent.move(), secondAgent);

        assertThat(board.matrix[1][1]).containsExactly(firstAgent, secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocations() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondAgent);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        when(secondAgent.move()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);
        board.moveAgent(zeroOnZeroCoordinate, secondAgent.move(), secondAgent);

        assertThat(board.matrix[0][1]).containsExactly(firstAgent);
        assertThat(board.matrix[1][0]).containsExactly(secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocationsSecondAgentFirst() {
        Board board = new Board(2, 2);
        board.placeAgent(zeroOnZeroCoordinate, firstAgent);
        board.placeAgent(zeroOnZeroCoordinate, secondAgent);
        when(firstAgent.move()).thenReturn(zeroOnOneCoordinate);
        when(secondAgent.move()).thenReturn(oneOnZeroCoordinate);

        board.moveAgent(zeroOnZeroCoordinate, secondAgent.move(), secondAgent);
        board.moveAgent(zeroOnZeroCoordinate, firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][1]).containsExactly(firstAgent);
        assertThat(board.matrix[1][0]).containsExactly(secondAgent);
    }
}