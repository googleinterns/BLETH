package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
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
    public void placeAnAgentOnBoardAgentShouldBeInRightLocation() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(zeroOnZeroCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(board.matrix[0][0]).containsExactly(firstAgent);
    }

    @Test
    public void placeAnAgentOnBoardAgentShouldNotBeInOtherLocation() {
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

    @Test
    public void canNotPlaceAnAgentOutsideTheBoardNegativeRow() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(negativeRowCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(isBoardEmpty(board, 2, 2)).isTrue();
    }

    @Test
    public void canNotPlaceAnAgentOutsideTheBoardNegativeCol() {
        Board board = new Board(2, 2);
        when(firstAgent.move()).thenReturn(negativeColCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(isBoardEmpty(board, 2, 2)).isTrue();
    }

    @Test
    public void canNotPlaceAnAgentOutsideTheBoardTooHighRow() {
        Board board = new Board(2, 2);
        Location rowTooHighCoordinate = new Location(2, 0);
        when(firstAgent.move()).thenReturn(rowTooHighCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(isBoardEmpty(board, 2, 2)).isTrue();
    }

    @Test
    public void canNotPlaceAnAgentOutsideTheBoardTooHighCol() {
        Board board = new Board(2, 2);
        Location colTooHighCoordinate = new Location(0, 2);
        when(firstAgent.move()).thenReturn(colTooHighCoordinate);

        board.placeAgent(firstAgent.move(), firstAgent);

        assertThat(isBoardEmpty(board, 2, 2)).isTrue();
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldBeInNewLocation() {

    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldNotBeInOldLocation() {

    }

    @Test
    public void moveAgentToNonEmptyLocation() {

    }

    @Test
    public void moveAgentFromEmptyLocation() {

    }

    @Test
    public void moveAgentFromNonEmptyLocation() {

    }

    @Test
    public void moveAgentToItsCurrentLocation() {

    }

    @Test
    public void tryMoveAgentOutsideTheBoard() {

    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToDifferentLocations() {

    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToSameLocation() {

    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocations() {

    }

    @Test
    public void moveTwoAgentsFromSameLocationToSameLocation() {

    }

    @Test
    public void moveTwoAgentsFromSameLocationSecondAgentFirst() {

    }
}

