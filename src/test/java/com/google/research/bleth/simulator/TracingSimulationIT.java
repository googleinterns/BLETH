package com.google.research.bleth.simulator;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static com.google.common.truth.Truth.assertThat;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationIT {

    private static final IMovementStrategy MOVE_UP = new UpMovementStrategy();
    private static final IMovementStrategy STATIONARY = new StationaryMovementStrategy();

    private static final double TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE = 1.0;
    private static final int AWAKENESS_CYCLE_EQUALS_TWO = 2;
    private static final int AWAKENESS_DURATION_EQUALS_ONE = 1;
    private static final AwakenessStrategyFactory.Type FIXED_AWAKENESS_STRATEGY_TYPE = AwakenessStrategyFactory.Type.FIXED;

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
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(MOVE_UP)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXED_AWAKENESS_STRATEGY_TYPE)
                .build();

        Map<IAgent, Location> initialAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getBoard());

        simulation.run();
        Map<IAgent, Location> terminalAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getBoard());

        for (IAgent agent : initialAgentsToLocations.keySet()) {
            assertThat(terminalAgentsToLocations.get(agent)).isEqualTo(
                       predictLocationAfterMoveUp(initialAgentsToLocations.get(agent), roundsNum));
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
                .setBeaconMovementStrategy(STATIONARY)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXED_AWAKENESS_STRATEGY_TYPE)
                .build();

        Map<IAgent, Location> initialAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getBoard());

        simulation.run();
        Map<IAgent, Location> terminalAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getBoard());

        for (IAgent agent : initialAgentsToLocations.keySet()) {
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
        double transmissionRadius = 2.0;

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXED_AWAKENESS_STRATEGY_TYPE)
                .build();

        Map<IAgent, Location> initialAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getBoard());
        Set<IAgent> observers = initialAgentsToLocations.keySet().stream().filter(agent -> agent instanceof Observer).collect(Collectors.toSet());
        Location observerInitialLocation = averageLocationOfObservers(observers);

        simulation.run();
        Map<IAgent, Location> estimatedAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getEstimatedBoard());

        Set<IAgent> beacons = initialAgentsToLocations.keySet().stream().filter(agent -> agent instanceof Beacon).collect(Collectors.toSet());
        for (IAgent beacon : beacons) {
            assertThat(estimatedAgentsToLocations.get(beacon)).isEqualTo(observerInitialLocation);
        }
    }

    @Test
    public void runTwoRoundsSimulationVerifyBeaconLocationOnEstimatedBoardAccordingToObserversLocations() {
        int roundsNum = 2;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 10;
        int observersNum = 5;
        double transmissionRadius = 2.0; // Includes the whole board

        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum + 1) // The first round is the initialization
                .setRowNum(rowsNum)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXED_AWAKENESS_STRATEGY_TYPE)
                .build();

        Map<IAgent, Location> initialAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getBoard());
        Set<IAgent> observers = initialAgentsToLocations.keySet().stream().filter(agent -> agent instanceof Observer).collect(Collectors.toSet());
        Location observersAverageLocation = averageLocationOfObservers(observers);

        simulation.run();
        Map<IAgent, Location> estimatedAgentsToLocations = mapAgentsToLocationsOnBoard(simulation.getEstimatedBoard());

        Set<IAgent> beacons = initialAgentsToLocations.keySet().stream().filter(agent -> agent instanceof Beacon).collect(Collectors.toSet());
        for (IAgent agent : beacons) {
            assertThat(estimatedAgentsToLocations.get(agent)).isEqualTo(observersAverageLocation);
        }
    }

    private Location averageLocationOfObservers(Set<IAgent> observers) {
        int newRow = (int) Math.round((observers.stream().mapToDouble(observer -> observer.getLocation().row()).average().getAsDouble()));
        int newCol = (int) Math.round((observers.stream().mapToDouble(observer -> observer.getLocation().col()).average().getAsDouble()));
        return Location.create(newRow, newCol);
    }

    private Map<IAgent, Location> mapAgentsToLocationsOnBoard(Board board) {
        return board.agentsOnBoard().entries().stream().collect(toImmutableMap(Map.Entry::getValue, Map.Entry::getKey));
    }

    private Location predictLocationAfterMoveUp(Location location, int numberOfRounds) {
        return Location.create(Math.max(0, location.row() - numberOfRounds), location.col());
    }
}
