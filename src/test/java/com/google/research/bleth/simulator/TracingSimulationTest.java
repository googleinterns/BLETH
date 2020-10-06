package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationTest {

    private final MovementStrategy moveUp = new moveUp();
    private final MovementStrategy stationary = new StationaryMovementStrategy();
    private final AwakenessStrategy dummyAwakenessStrategy = new DummyAwakenessStrategy();

    private static final String SIMULATION_ID = "test-sim-id-1";
    private static final int BOARD_DIMENSION_EQUALS_TWO = 2;
    private static final int BOARD_DIMENSION_EQUALS_THREE = 3;
    private static final int MAX_ROUNDS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_BEACONS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_OBSERVERS_EQUALS_TWO = 2;

    private static final int AWAKENESS_CYCLE_EQUALS_TWO = 2;
    private static final int AWAKENESS_DURATION_EQUALS_ONE = 1;
    private static final double RADIUS_EQUALS_ONE = 1.0;

    private static final int ILLEGAL_BOARD_DIMENSION = 0;
    private static final int ILLEGAL_NUMBER_OF_AGENTS = 0;
    private static final int ILLEGAL_MAX_ROUNDS = 0;
    private static final int ILLEGAL_RADIUS = -1;

    @Mock
    private Beacon beacon1;

    @Mock
    private Beacon beacon2;

    @Mock
    private Observer observer1;

    @Mock
    private Observer observer2;

    @Mock
    private GlobalResolver resolver;

    // Test cases focused on building a TracingSimulation from scratch.

    @Test
    public void setIllegalBoardDimensionInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(ILLEGAL_BOARD_DIMENSION)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalMaxRoundsInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(ILLEGAL_MAX_ROUNDS)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalRadiusInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(ILLEGAL_RADIUS)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalNumberOfAgentsInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(ILLEGAL_NUMBER_OF_AGENTS)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingBeaconMovementStrategyInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingObserverMovementStrategyInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingAwakenessStrategyInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingBoardDimensionInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingNumberOfAgentsInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingMaxRoundsNumberInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingRadiusInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    // Test cases focused on building a TracingSimulation from existing.

    @Test
    public void initSimulationFromExistingNumberOfBeaconsShouldBeUpdated() {
        Board realBoard = new Board(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        Board estimatedBoard = new Board(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);
        Mockito.when(resolver.getBoard()).thenReturn(estimatedBoard);

        Simulation simulation = new TracingSimulation.TracingSimulationBuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(realBoard)
                .setResolver(resolver)
                .setBeacons(beacons)
                .setObservers(observers)
                .build();

        assertThat(simulation.beaconsNum).isEqualTo(NUMBER_OF_BEACONS_EQUALS_TWO);
    }

    @Test
    public void initSimulationFromExistingWithNonGlobalResolverShouldThrowException() {
        Board realBoard = new Board(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        Simulation.SimulationBuilderFromExisting builder = new TracingSimulation.TracingSimulationBuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(realBoard)
                .setResolver(new FakeResolver())
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void initSimulationFromExistingDifferentBoardsDimensionsShouldThrowException() {
        Board realBoard = new Board(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        Board estimatedBoard = new Board(BOARD_DIMENSION_EQUALS_THREE, BOARD_DIMENSION_EQUALS_THREE);
        Mockito.when(resolver.getBoard()).thenReturn(estimatedBoard);
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        Simulation.SimulationBuilderFromExisting builder = new TracingSimulation.TracingSimulationBuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(realBoard)
                .setResolver(resolver)
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void initSimulationFromExistingWithoutResolverShouldThrowException() {
        Board board = new Board(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        Simulation.SimulationBuilderFromExisting builder = new TracingSimulation.TracingSimulationBuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(board)
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void initSimulationFromExistingWithoutAgentsShouldThrowException() {
        Board board = new Board(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);

        Simulation.SimulationBuilderFromExisting builder = new TracingSimulation.TracingSimulationBuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(board)
                .setResolver(resolver);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void initSimulationFromExistingWithoutBoardShouldThrowException() {
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        Simulation.SimulationBuilderFromExisting builder = new TracingSimulation.TracingSimulationBuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .setRadius(RADIUS_EQUALS_ONE)
                .setResolver(resolver)
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(NullPointerException.class, builder::build);
    }

    // Test cases focused on Tracing Simulation logic.

    @Test
    public void initializeTwoBeaconsBeaconsContainerShouldEqualTwo() {
        Simulation simulation = new TracingSimulation
                .TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .build();

        simulation.initializeBeacons();

        assertThat(simulation.getBeacons().size()).isEqualTo(NUMBER_OF_BEACONS_EQUALS_TWO);
    }

    @Test
    public void runSimulationWithMaxRoundNumberTwoCurrentRoundNumberShouldBeTwo() {
        Simulation simulation = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .build();

        simulation.run();

        assertThat(simulation.currentRound).isEqualTo(MAX_ROUNDS_EQUALS_TWO);
    }

    @Test
    public void moveAgentsDeterministicMovementStrategyBoardShouldEqualExpectedBoard() {
        Simulation simulation = new TracingSimulation.TracingSimulationBuilder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(moveUp)
                .setObserverMovementStrategy(stationary)
                .setAwakenessStrategy(dummyAwakenessStrategy)
                .build();

        simulation.initializeBeacons();
        Board boardWhenSimulationInitialized = simulation.getBoard();
        Board expectedBoardAfterSingleRound =
                buildExpectedBoard(boardWhenSimulationInitialized, BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        simulation.moveAgents();
        Board boardAfterMovement = simulation.getBoard();

        assertThat(boardsEqual(boardAfterMovement, expectedBoardAfterSingleRound,
                BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO)).isTrue();
    }

    // Helper methods.

    private static class moveUp implements MovementStrategy {

        @Override
        public Location moveTo(Board board, Location currentLocation) {
            Location newLocation = currentLocation.moveInDirection(Direction.UP);
            if (board.isLocationValid(newLocation)) {
                return newLocation;
            }
            return currentLocation;
        }
    }

    private static class DummyAwakenessStrategy implements AwakenessStrategy { }

    private Board buildExpectedBoard(Board originalBoard, int rowNum, int colNum) {

        Board expectedBoard = new Board(rowNum, colNum);
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                for (Agent agent : originalBoard.getAgentsOnLocation(new Location(row, col))) {
                    expectedBoard.placeAgent(agent.moveTo(), agent);
                }
            }
        }
        return expectedBoard;
    }

    private boolean boardsEqual(Board actual, Board expected, int rowNum, int colNum) {
        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                Location loc = new Location(row, col);
                if (!actual.getAgentsOnLocation(loc).equals(expected.getAgentsOnLocation(loc))) {
                    return false;
                }
            }
        }
        return true;
    }

    private class FakeResolver implements IResolver {
        @Override
        public void receiveInformation(Location observerLocation, List<Transmission> transmissions) {

        }

        @Override
        public Board getBoard() {
            return null;
        }
    }
}
