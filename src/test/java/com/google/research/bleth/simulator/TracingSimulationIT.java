package com.google.research.bleth.simulator;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
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
    public void runTwoRoundsSimulationWithOneObserverVerifyAllBeaconsHaveBeenObservedOnce() {
        int roundsNum = 2;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 10;
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

        simulation.run();
        String simulationId = simulation.getId();

        Map<String, Double> observersStats = StatisticsState.readBeaconsObservedPercentStats(simulationId);

        for (String beacon : observersStats.keySet()) {
            assertThat(observersStats.get(beacon)).isEqualTo(0.5);
        }
    }

    @Test
    public void runTwoRoundsSimulationWithHundredObserversVerifyAllBeaconsHaveBeenObservedAllTheTime() {
        int roundsNum = 2;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 10;
        int observersNum = 100;
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

        simulation.run();
        String simulationId = simulation.getId();

        Map<String, Double> observersStats = StatisticsState.readBeaconsObservedPercentStats(simulationId);

        for (String beacon : observersStats.keySet()) {
            assertThat(observersStats.get(beacon)).isEqualTo(1);
        }
    }

    @Test
    public void runFourRoundsSimulationWithOneObserverVerifyAllBeaconsHaveBeenObservedTwice() {
        int roundsNum = 4;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 10;
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

        simulation.run();
        String simulationId = simulation.getId();

        Map<String, Double> observersStats = StatisticsState.readBeaconsObservedPercentStats(simulationId);

        for (String beacon : observersStats.keySet()) {
            assertThat(observersStats.get(beacon)).isEqualTo(0.5);
        }
    }

    @Test
    public void runTwoRoundsSimulationWithOnOneOnOneBoardDistancesEstimatedIsZero() {
        int roundsNum = 2;
        int rowsNum = 1;
        int colsNum = 1;
        int beaconsNum = 10;
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

        simulation.run();
        String simulationId = simulation.getId();

        Map<String, Double> distancesStats = StatisticsState.readDistancesStats(simulationId);

        for (String aggregateFunction : distancesStats.keySet()) {
            assertThat(distancesStats.get(aggregateFunction)).isEqualTo(0.0);
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
        assertThat(distancesStats.get("min")).isEqualTo(0.0);
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
