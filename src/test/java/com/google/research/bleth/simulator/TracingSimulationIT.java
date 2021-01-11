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

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Table;
import com.google.research.bleth.exceptions.StatisticsAlreadyExistException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationIT {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SCATTERED));

    private static final MovementStrategyFactory.Type MOVE_UP = MovementStrategyFactory.Type.UP;
    private static final MovementStrategyFactory.Type STATIONARY = MovementStrategyFactory.Type.STATIONARY;
    private static final double TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE = 1.0;
    private static final int AWAKENESS_CYCLE_EQUALS_TWO = 2;
    private static final int AWAKENESS_DURATION_EQUALS_ONE = 1;

    @Before
    public void setUp() {
        helper.setUp();
    }

    @Test
    public void runTwoRoundsSimulationVerifyMovingUpAgentsLocations() {
        int roundsNum = 2;
        int rowsNum = 5;
        int colsNum = 5;
        int beaconsNum = 10;
        int observersNum = 10;

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(MOVE_UP)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        Map<String, Location> initialAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getRealBoardState());

        simulation.run();
        Map<String, Location> terminalAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getRealBoardState());

        for (String agent : initialAgentsToLocations.keySet()) {
            assertThat(terminalAgentsToLocations.get(agent))
                      .isEqualTo(predictLocationAfterMoveUp(initialAgentsToLocations.get(agent), roundsNum));
        }
    }

    @Test
    public void runTwoRoundsSimulationVerifyStationaryAgentsLocations() {
        int roundsNum = 2;
        int rowsNum = 5;
        int colsNum = 5;
        int beaconsNum = 10;
        int observersNum = 10;

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(STATIONARY)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        Map<String, Location> initialAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getRealBoardState());

        simulation.run();
        Map<String, Location> terminalAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getRealBoardState());

        for (String agent : initialAgentsToLocations.keySet()) {
            assertThat(terminalAgentsToLocations.get(agent)).isEqualTo(initialAgentsToLocations.get(agent));
        }
    }

    @Test
    public void runTwoRoundsSimulationVerifyBeaconLocationOnEstimatedBoardAccordingToObserverLocation() {
        int roundsNum = 2;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 1;
        int observersNum = 1;
        double transmissionRadius = 2.0; // Includes the whole board

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        Map<String, Location> initialAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getRealBoardState());
        Location observerInitialLocation = Iterables.getOnlyElement(simulation.observers).getLocation();

        simulation.run();
        Map<String, Location> estimatedAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getEstimatedBoardState());

        Set<String> beacons = initialAgentsToLocations.keySet().stream()
                              .filter(agent -> agent.startsWith("Beacon")).collect(Collectors.toSet());
        for (String beacon : beacons) {
            assertThat(estimatedAgentsToLocations.get(beacon)).isEqualTo(observerInitialLocation);
        }
    }

    @Test
    public void runTwoRoundsSimulationVerifyBeaconLocationOnEstimatedBoardAccordingToObserversLocations() {
        int roundsNum = 2;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 10;
        int observersNum = 10;
        double transmissionRadius = 2.0; // Includes the whole board
        int awakenessCycle = 1; // all the observers are awake at the same time

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(awakenessCycle)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        Map<String, Location> initialAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getRealBoardState());
        Location observersAverageLocation = averageLocationOfObservers(simulation.observers);

        simulation.run();
        Map<String, Location> estimatedAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getEstimatedBoardState());

        Set<String> beacons = initialAgentsToLocations.keySet().stream()
                              .filter(agent -> agent.startsWith("Beacon")).collect(Collectors.toSet());
        for (String agent : beacons) {
            assertThat(estimatedAgentsToLocations.get(agent)).isEqualTo(observersAverageLocation);
        }
    }

    // tests for gathering statistics

    @Test
    public void runTwoRoundsSimulationWithOnOneOnOneBoardDistancesEstimatedIsZero() {
        int roundsNum = 2;
        int rowsNum = 1;
        int colsNum = 1;
        int beaconsNum = 10;
        int observersNum = 1;
        double transmissionRadius = 2.0; // Includes the whole board

        double distanceFromBeacons = 0.0; // The observer and the beacons are in the only location in the board

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        simulation.run();
        String simulationId = simulation.getId();

        Map<String, Double> distancesStats = StatisticsState.readDistancesStats(simulationId);

        for (String aggregateFunction : distancesStats.keySet()) {
            assertThat(distancesStats.get(aggregateFunction)).isEqualTo(distanceFromBeacons);
        }
    }

    @Test
    public void runTwoRoundsSimulationWithHundredStationaryBeaconsMaxDistanceIsRadiusAndMinIsZero() {
        int roundsNum = 2;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 100;
        int observersNum = 1;
        double transmissionRadius = 2.0; // Includes the whole board

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategyType(STATIONARY)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        simulation.run();
        String simulationId = simulation.getId();

        Map<String, Double> distancesStats = StatisticsState.readDistancesStats(simulationId);

        assertThat(distancesStats.get("max")).isEqualTo(transmissionRadius);
        assertThat(distancesStats.get("min")).isEqualTo(0.0); // the observer shares its location with some beacons
    }

    @Test
    public void writeDistancesStatisticsOfSameSimulationTwiceThrowsException() {
        Map<String, Double> fakeStats = new HashMap<>();
        LinkedListMultimap<Integer, ObservedInterval> fakeObservedIntervals = LinkedListMultimap.create();
        Table<String, String, Double> fakeObservedStats = HashBasedTable.create();
        fakeStats.put("max", 0D);
        StatisticsState statistics = StatisticsState.create("1", fakeStats, fakeObservedStats, fakeObservedIntervals);

        statistics.writeDistancesStats();

        assertThrows(StatisticsAlreadyExistException.class, () -> {
            statistics.writeDistancesStats();
        });
    }

    @Test
    public void readNonExistingSimulationDistancesStatisticsReturnsEmptyMap() {
        Map<String, Double> distancesStatistics = StatisticsState.readDistancesStats("fake");

        assertThat(distancesStatistics).isEmpty();
    }

    @Test
    public void runSimulationWithOneObserverAwakeEveryOtherRoundVerifyObservedStats() {
        int roundsNum = 10;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 10;
        int observersNum = 1;
        double transmissionRadius = 2.0; // Includes the whole board

        double observerAwakenessRatio = (double) AWAKENESS_DURATION_EQUALS_ONE / AWAKENESS_CYCLE_EQUALS_TWO;
        double observerAwakenessInterval = 1;
        double observerSleepingInterval = 1;

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        simulation.run();
        String simulationId = simulation.getId();

        Map<String, Map<String, Double>> observedStats = StatisticsState.readBeaconsObservedStats(simulationId).rowMap();

        for (String beacon : observedStats.keySet()) {
            Map<String, Double> stats = observedStats.get(beacon);
            assertThat(stats.get(Schema.StatisticsState.observedPercent)).isEqualTo(observerAwakenessRatio);
            assertThat(stats.get(Schema.StatisticsState.minimumLengthObservedInterval)).isEqualTo(observerAwakenessInterval);
            assertThat(stats.get(Schema.StatisticsState.minimumLengthUnobservedInterval)).isEqualTo(observerSleepingInterval);
            assertThat(stats.get(Schema.StatisticsState.maximumLengthObservedInterval)).isEqualTo(observerAwakenessInterval);
            assertThat(stats.get(Schema.StatisticsState.maximumLengthUnobservedInterval)).isEqualTo(observerSleepingInterval);
            assertThat(stats.get(Schema.StatisticsState.averageLengthObservedInterval)).isEqualTo(observerAwakenessInterval);
            assertThat(stats.get(Schema.StatisticsState.averageLengthUnobservedInterval)).isEqualTo(observerSleepingInterval);
        }
    }

    @Test
    public void runSimulationWithHundredObserversSoAllBeaconsHaveBeenObservedAllTheTimeVerifyObservedStats() {
        int roundsNum = 2;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 10;
        int observersNum = 100;
        double transmissionRadius = 2.0; // Includes the whole board

        double observersAwakenessRatio = 1; // At least one observer is awake each round

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        simulation.run();
        String simulationId = simulation.getId();

        Map<String, Map<String, Double>> observedStats = StatisticsState.readBeaconsObservedStats(simulationId).rowMap();

        for (String beacon : observedStats.keySet()) {
            Map<String, Double> stats = observedStats.get(beacon);
            assertThat(stats.get(Schema.StatisticsState.observedPercent)).isEqualTo(observersAwakenessRatio);
            assertThat(stats.get(Schema.StatisticsState.minimumLengthObservedInterval)).isEqualTo(roundsNum);
            assertThat(stats.get(Schema.StatisticsState.minimumLengthUnobservedInterval)).isEqualTo(Double.NaN);
            assertThat(stats.get(Schema.StatisticsState.maximumLengthObservedInterval)).isEqualTo(roundsNum);
            assertThat(stats.get(Schema.StatisticsState.maximumLengthUnobservedInterval)).isEqualTo(Double.NaN);
            assertThat(stats.get(Schema.StatisticsState.averageLengthObservedInterval)).isEqualTo(roundsNum);
            assertThat(stats.get(Schema.StatisticsState.averageLengthUnobservedInterval)).isEqualTo(Double.NaN);
        }
    }

    @Test
    public void writeObservedStatisticsOfSameSimulationTwiceThrowsException() {
        Map<String, Double> fakeStats = new HashMap<>();
        Table<String, String, Double> fakeObservedStats = HashBasedTable.create();
        LinkedListMultimap<Integer, ObservedInterval> fakeObservedIntervals = LinkedListMultimap.create();
        fakeObservedStats.put("0", Schema.StatisticsState.observedPercent, 0D);
        StatisticsState statistics = StatisticsState.create("1", fakeStats, fakeObservedStats, fakeObservedIntervals);

        statistics.writeBeaconsObservedStats();

        assertThrows(StatisticsAlreadyExistException.class, () -> {
            statistics.writeBeaconsObservedStats();
        });
    }

    @Test
    public void readNonExistingSimulationObservedStatisticsReturnsEmptyMap() {
        Table<String, String, Double> observedStats = StatisticsState.readBeaconsObservedStats("fake");

        assertThat(observedStats).isEmpty();
    }

    @Test
    public void compareIntervalStatsFromSimulationAndFromDatabase() {
        int roundsNum = 5;
        int rowsNum = 5;
        int colsNum = 5;
        int beaconsNum = 10;
        int observersNum = 10;

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum)
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(MOVE_UP)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        simulation.run();
        ImmutableMultimap<Integer, ObservedInterval> expected = simulation.getBeaconsObservedIntervals();
        ImmutableMultimap<Integer, ObservedInterval> actual = StatisticsState.readIntervalStats(simulation.getId());

        assertThat(actual).containsExactlyEntriesIn(expected);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private Location averageLocationOfObservers(Collection<Observer> observers) {
        int newRow = (int) Math.round((observers.stream().mapToDouble(observer -> observer.getLocation().row()).average().getAsDouble()));
        int newCol = (int) Math.round((observers.stream().mapToDouble(observer -> observer.getLocation().col()).average().getAsDouble()));
        return Location.create(newRow, newCol);
    }

    private Map<String, Location> mapAgentsToLocationsOnBoard(BoardState boardState) {
        return boardState.agentsRepresentationsOnStateBoard().entries().stream().collect(
                         toImmutableMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private Location predictLocationAfterMoveUp(Location location, int numberOfRounds) {
        return Location.create(Math.max(0, location.row() - numberOfRounds), location.col());
    }
}
