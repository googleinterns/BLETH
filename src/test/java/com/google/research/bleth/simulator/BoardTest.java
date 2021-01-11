// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

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
    private static final Location NEGATIVE_ROW_COORDINATE = Location.create(-1, 0);
    private static final Location NEGATIVE_COL_COORDINATE = Location.create(0, -1);
    private static final Location ZERO_ON_ZERO_COORDINATE = Location.create(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = Location.create(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = Location.create(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = Location.create(1, 0);

    @Mock
    private IAgent firstAgent;
    @Mock
    private IAgent secondAgent;

    @Test
    public void newBoardIsEmpty() {
        AbstractBoard board = new RealBoard(2, 2);

        assertThat(board.agentsOnBoard()).isEmpty();
    }

    @Test
    public void placeAnAgentOnBoardAgentShouldBeOnRightLocationAndShouldNotBeOnOtherLocations() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        board.placeAgent(firstAgent.moveTo(), firstAgent);

        assertThat(board.agentsOnBoard()).containsExactly(ZERO_ON_ZERO_COORDINATE, firstAgent);
    }

    @Test
    public void placeTwoAgentsOnDifferentLocation() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(secondAgent.moveTo()).thenReturn(ONE_ON_ONE_COORDINATE);

        board.placeAgent(firstAgent.moveTo(), firstAgent);
        board.placeAgent(secondAgent.moveTo(), secondAgent);

        assertThat(board.agentsOnBoard())
                .containsExactly(ZERO_ON_ZERO_COORDINATE, firstAgent,
                                 ONE_ON_ONE_COORDINATE, secondAgent);
    }

    @Test
    public void placeTwoAgentsOnSameLocation() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.placeAgent(firstAgent.moveTo(), firstAgent);
        board.placeAgent(secondAgent.moveTo(), secondAgent);

        assertThat(board.agentsOnBoard())
                .containsExactly(ZERO_ON_ONE_COORDINATE, firstAgent,
                                 ZERO_ON_ONE_COORDINATE, secondAgent);
    }

    @Test
    public void placeNullOnBoardThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        assertThrows(NullPointerException.class, () -> {
            board.placeAgent(firstAgent.moveTo(), null);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardNegativeRowThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(NEGATIVE_ROW_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardNegativeColThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(NEGATIVE_COL_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardTooHighRowThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Location rowTooHighCoordinate = Location.create(2, 0);
        Mockito.when(firstAgent.moveTo()).thenReturn(rowTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void placeAnAgentOutsideTheBoardTooHighColThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Location colTooHighCoordinate = Location.create(0, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(colTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.placeAgent(firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAgentToEmptyLocationAgentShouldBeOnNewLocationAndShouldNotBeOnOldLocation() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);

        assertThat(board.agentsOnBoard()).containsExactly(ZERO_ON_ONE_COORDINATE, firstAgent);
    }

    @Test
    public void moveAgentToNonEmptyLocation() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        board.placeAgent(ZERO_ON_ONE_COORDINATE, secondAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);

        assertThat(board.agentsOnBoard()).
                containsExactly(ZERO_ON_ONE_COORDINATE, firstAgent,
                                ZERO_ON_ONE_COORDINATE, secondAgent);
    }

    @Test
    public void moveAgentFromNonEmptyLocation() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, secondAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);

        assertThat(board.agentsOnBoard())
                .containsExactly(ZERO_ON_ZERO_COORDINATE, secondAgent,
                                 ZERO_ON_ONE_COORDINATE, firstAgent);
    }

    @Test
    public void moveAgentToItsCurrentLocation() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);

        assertThat(board.agentsOnBoard()).containsExactly(ZERO_ON_ZERO_COORDINATE, firstAgent);
    }

    @Test
    public void moveNullOnBoardThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ONE_ON_ZERO_COORDINATE);

        assertThrows(NullPointerException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), null);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardNegativeRowThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(NEGATIVE_ROW_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardNegativeColThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(NEGATIVE_COL_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardTooHighRowThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        Location rowTooHighCoordinate = Location.create(2, 0);
        Mockito.when(firstAgent.moveTo()).thenReturn(rowTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentOutsideTheBoardTooHighColThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        Location colTooHighCoordinate = Location.create(0, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(colTooHighCoordinate);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentFromNegativeRowOutsideTheBoardThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(NEGATIVE_ROW_COORDINATE, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentFromNegativeColOutsideTheBoardThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(NEGATIVE_COL_COORDINATE, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentFromTooHighRowOutsideTheBoardThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);
        Location rowTooHighCoordinate = Location.create(2, 0);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(rowTooHighCoordinate, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentFromTooHighColOutsideTheBoardThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ZERO_COORDINATE);
        Location colTooHighCoordinate = Location.create(0, 2);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(colTooHighCoordinate, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveAnAgentFromOutsideTheBoardToOutsideTheBoardThrowsException() {
        AbstractBoard board = new RealBoard(2, 2);
        Mockito.when(firstAgent.moveTo()).thenReturn(NEGATIVE_COL_COORDINATE);

        assertThrows(IllegalArgumentException.class, () -> {
            board.moveAgent(NEGATIVE_ROW_COORDINATE, firstAgent.moveTo(), firstAgent);
        });
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToDifferentLocations() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        board.placeAgent(ONE_ON_ONE_COORDINATE, secondAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondAgent.moveTo()).thenReturn(ONE_ON_ZERO_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);
        board.moveAgent(ONE_ON_ONE_COORDINATE, secondAgent.moveTo(), secondAgent);

        assertThat(board.agentsOnBoard())
                .containsExactly(ZERO_ON_ONE_COORDINATE, firstAgent,
                                 ONE_ON_ZERO_COORDINATE, secondAgent);
    }

    @Test
    public void moveTwoAgentsFromDifferentLocationsToSameLocation() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        board.placeAgent(ONE_ON_ONE_COORDINATE, secondAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);
        board.moveAgent(ONE_ON_ONE_COORDINATE, secondAgent.moveTo(), secondAgent);

        assertThat(board.agentsOnBoard())
                .containsExactly(ZERO_ON_ONE_COORDINATE, firstAgent,
                                 ZERO_ON_ONE_COORDINATE, secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToSameLocation() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, secondAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ONE_ON_ONE_COORDINATE);
        Mockito.when(secondAgent.moveTo()).thenReturn(ONE_ON_ONE_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);
        board.moveAgent(ZERO_ON_ZERO_COORDINATE, secondAgent.moveTo(), secondAgent);

        assertThat(board.agentsOnBoard())
                .containsExactly(ONE_ON_ONE_COORDINATE, firstAgent,
                                 ONE_ON_ONE_COORDINATE, secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocations() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, secondAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondAgent.moveTo()).thenReturn(ONE_ON_ZERO_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);
        board.moveAgent(ZERO_ON_ZERO_COORDINATE, secondAgent.moveTo(), secondAgent);

        assertThat(board.agentsOnBoard())
                .containsExactly(ZERO_ON_ONE_COORDINATE, firstAgent,
                                 ONE_ON_ZERO_COORDINATE, secondAgent);
    }

    @Test
    public void moveTwoAgentsFromSameLocationToDifferentLocationsSecondAgentFirst() {
        AbstractBoard board = new RealBoard(2, 2);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, firstAgent);
        board.placeAgent(ZERO_ON_ZERO_COORDINATE, secondAgent);
        Mockito.when(firstAgent.moveTo()).thenReturn(ZERO_ON_ONE_COORDINATE);
        Mockito.when(secondAgent.moveTo()).thenReturn(ONE_ON_ZERO_COORDINATE);

        board.moveAgent(ZERO_ON_ZERO_COORDINATE, secondAgent.moveTo(), secondAgent);
        board.moveAgent(ZERO_ON_ZERO_COORDINATE, firstAgent.moveTo(), firstAgent);

        assertThat(board.agentsOnBoard())
                .containsExactly(ZERO_ON_ONE_COORDINATE, firstAgent,
                                 ONE_ON_ZERO_COORDINATE, secondAgent);
    }
}