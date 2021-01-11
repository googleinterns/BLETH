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

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.research.bleth.exceptions.BoardStateAlreadyExistsException;
import com.google.research.bleth.exceptions.ExceedingRoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DatastoreIT {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SCATTERED));

    private static final Location ZERO_ON_ZERO_COORDINATE = Location.create(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = Location.create(1, 1);
    private static final int BOARD_DIMENSION = 2;
    private static final int ZERO_ROUND = 0;
    private static final int ONE_ROUND = 1;
    private static final int MAX_NUMBER_OF_ROUNDS = 100;

    private static final AbstractSimulation.Builder firstSimulationBuilder = new FakeSimulation.Builder();
    private static final AbstractSimulation.Builder secondSimulationBuilder = new FakeSimulation.Builder();

    @Mock
    Beacon beacon;
    @Mock
    Observer observer;

    @Before
    public void setUp() {
        helper.setUp();
    }

    @Before
    public void mockAgents() {
        Mockito.when(beacon.getId()).thenReturn(0);
        Mockito.when(beacon.getType()).thenReturn("Beacon");
        Mockito.when(observer.getId()).thenReturn(0);
        Mockito.when(observer.getType()).thenReturn("Observer");
    }

    @Test
    public void writeEmptyRealBoardThenReadRealBoard_shouldGetExpectedBoardState() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();
        BoardState expectedBoardState =
                BoardStateFactory.create(realBoard, firstSimulationId, ZERO_ROUND);

        expectedBoardState.write();
        BoardState readBoardState = BoardState.readReal(firstSimulationId, ZERO_ROUND);

        assertThat(readBoardState).isEqualTo(expectedBoardState);
    }

    @Test
    public void writeNonEmptyRealBoardThenReadRealBoard_shouldGetExpectedBoardState() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();
        BoardState expectedBoardState =
                BoardStateFactory.create(realBoard, firstSimulationId, ZERO_ROUND);

        expectedBoardState.write();
        BoardState readBoardState = BoardState.readReal(firstSimulationId, ZERO_ROUND);

        assertThat(readBoardState).isEqualTo(expectedBoardState);
    }

    @Test
    public void writeNonEmptyEstimatedBoardThenReadEstimatedBoard_shouldGetExpectedBoardState() {
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        estimatedBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        estimatedBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();
        BoardState expectedBoardState =
                BoardStateFactory.create(estimatedBoard, firstSimulationId, ZERO_ROUND);

        expectedBoardState.write();
        BoardState readBoardState = BoardState.readEstimated(firstSimulationId, ZERO_ROUND);

        assertThat(readBoardState).isEqualTo(expectedBoardState);
    }

    @Test
    public void writeOnlyRealBoardThenReadEstimatedBoard_shouldGetEmptyBoardState() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();
        BoardState expectedBoardState =
                BoardStateFactory.create(new EstimatedBoard(BOARD_DIMENSION, BOARD_DIMENSION), firstSimulationId, ZERO_ROUND);

        BoardState realBoardState =
                BoardStateFactory.create(realBoard, firstSimulationId, ZERO_ROUND);
        realBoardState.write();
        BoardState readBoardState = BoardState.readEstimated(firstSimulationId, ZERO_ROUND);

        assertThat(readBoardState).isEqualTo(expectedBoardState);
    }

    @Test
    public void writeRealBoardThenReadRealBoardWithDifferentSimulationId_shouldGetEmptyBoardState() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();
        String secondSimulationId = secondSimulationBuilder.writeMetadata();
        BoardState expectedBoardState =
                BoardStateFactory.create(new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION), secondSimulationId, ZERO_ROUND);

        BoardState realBoardState =
                BoardStateFactory.create(realBoard, firstSimulationId, ZERO_ROUND);
        realBoardState.write();
        BoardState readBoardState = BoardState.readReal(secondSimulationId, ZERO_ROUND);

        assertThat(readBoardState).isEqualTo(expectedBoardState);
    }

    @Test
    public void writeEstimatedBoardThenReadEstimatedBoardWithDifferentExistingRound_shouldGetEmptyBoardState() {
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        estimatedBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        estimatedBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();
        BoardState expectedBoardState =
                BoardStateFactory.create(new EstimatedBoard(BOARD_DIMENSION, BOARD_DIMENSION), firstSimulationId, ONE_ROUND);

        BoardState estimatedBoardState =
                BoardStateFactory.create(estimatedBoard, firstSimulationId, ZERO_ROUND);
        estimatedBoardState.write();
        BoardState readBoardState = BoardState.readEstimated(firstSimulationId, ONE_ROUND);

        assertThat(readBoardState).isEqualTo(expectedBoardState);
    }

    @Test
    public void writeEstimatedBoardThenReadEstimatedBoardWithDifferentNonExistingRound_shouldThrowException() {
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        estimatedBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        estimatedBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();

        BoardState estimatedBoardState =
                BoardStateFactory.create(estimatedBoard, firstSimulationId, ZERO_ROUND);
        estimatedBoardState.write();

        assertThrows(ExceedingRoundException.class, () -> {
            BoardState.readEstimated(firstSimulationId, MAX_NUMBER_OF_ROUNDS);
        });
    }

    @Test
    public void createRealBoardStateWithNonExistingRound_shouldThrowException() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();

        assertThrows(ExceedingRoundException.class, () -> {
            BoardStateFactory.create(realBoard, firstSimulationId, MAX_NUMBER_OF_ROUNDS);
        });
    }

    @Test
    public void writeDuplicateSimulationIdAndRoundBoardState_shouldThrowException() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION, BOARD_DIMENSION);
        realBoard.placeAgent(ZERO_ON_ZERO_COORDINATE, beacon);
        realBoard.placeAgent(ONE_ON_ONE_COORDINATE, observer);
        String firstSimulationId = firstSimulationBuilder.writeMetadata();
        BoardState boardState =
                BoardStateFactory.create(realBoard, firstSimulationId, ZERO_ROUND);

        boardState.write();

        assertThrows(BoardStateAlreadyExistsException.class, () -> boardState.write());
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    public static class FakeSimulation extends AbstractSimulation {
        protected FakeSimulation(AbstractSimulation.Builder builder) {
            super(builder);
        }

        public static class Builder extends AbstractSimulation.Builder {
            public String getSimulationType() {
                return "Fake";
            }

            public int getRowNum() {
                return BOARD_DIMENSION;
            }

            public int getColNum() {
                return BOARD_DIMENSION;
            }

            public int getMaxNumberOfRounds() {
                return MAX_NUMBER_OF_ROUNDS;
            }

            public MovementStrategyFactory.Type getBeaconMovementStrategyType() {
                return MovementStrategyFactory.Type.RANDOM;
            }

            public MovementStrategyFactory.Type getObserverMovementStrategyType() {
                return MovementStrategyFactory.Type.STATIONARY;
            }

            public AwakenessStrategyFactory.Type getAwakenessStrategyType() {
                return AwakenessStrategyFactory.Type.FIXED;
            }

            @Override
            public void validateArguments() { }

            @Override
            void initializeObservers() { }

            @Override
            void initializeBeacons() { }

            @Override
            public AbstractSimulation build() { return null; }
        }
    }
}
