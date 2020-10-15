package com.google.research.bleth.simulator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationTest {

    private static final IMovementStrategy MOVE_UP = new moveUp();
    private static final IMovementStrategy STATIONARY = new StationaryMovementStrategy();
    private static final AwakenessStrategyFactory DUMMY_AWAKENESS_STRATEGY_FACTORY =
            new AwakenessStrategyFactory(AwakenessStrategyFactory.Type.FIXED);

    private static final String SIMULATION_ID = "test-sim-id-1";
    private static final int BOARD_DIMENSION_EQUALS_TWO = 2;
    private static final int BOARD_DIMENSION_EQUALS_THREE = 3;
    private static final int MAX_ROUNDS_EQUALS_TWO = 2;
    private static final int CURRENT_ROUND_EQUALS_THREE = 3;
    private static final int NUMBER_OF_BEACONS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_OBSERVERS_EQUALS_TWO = 2;
    private static final double RADIUS_EQUALS_ONE = 1.0;
    private static final int AWAKENESS_CYCLE_EQUALS_TWO = 2;
    private static final int AWAKENESS_DURATION_EQUALS_ONE = 1;
    private static final int AWAKENESS_DURATION_EQUALS_THREE = 3;
    private static final AwakenessStrategyFactory.Type FIXES_AWAKENESS_STRATEGY_TYPE =
            AwakenessStrategyFactory.Type.FIXED;

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
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(ILLEGAL_BOARD_DIMENSION)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalAwakenessCycleAndDurationInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_THREE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalMaxRoundsInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(ILLEGAL_MAX_ROUNDS)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalRadiusInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(ILLEGAL_RADIUS)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalNumberOfAgentsInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(ILLEGAL_NUMBER_OF_AGENTS)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingBeaconMovementStrategyInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingObserverMovementStrategyInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingAwakenessStrategyTypeInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setObserverMovementStrategy(STATIONARY);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingBoardDimensionInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingNumberOfAgentsInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingMaxRoundsNumberInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingRadiusInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    // Test cases focused on building a TracingSimulation from existing.

    @Test
    public void initSimulationFromExistingWithNonGlobalResolverShouldThrowException() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        AbstractSimulation.BuilderFromExisting builder = new TracingSimulation.BuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessStrategyFactory(DUMMY_AWAKENESS_STRATEGY_FACTORY)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(realBoard)
                .setResolver(new FakeResolver())
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void initSimulationFromExistingDifferentBoardsDimensionsShouldThrowException() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION_EQUALS_THREE, BOARD_DIMENSION_EQUALS_THREE);
        Mockito.when(resolver.getBoard()).thenReturn(estimatedBoard);
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        AbstractSimulation.BuilderFromExisting builder = new TracingSimulation.BuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessStrategyFactory(DUMMY_AWAKENESS_STRATEGY_FACTORY)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(realBoard)
                .setResolver(resolver)
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void initSimulationFromExistingWithoutResolverShouldThrowException() {
        RealBoard board = new RealBoard(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        AbstractSimulation.BuilderFromExisting builder = new TracingSimulation.BuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessStrategyFactory(DUMMY_AWAKENESS_STRATEGY_FACTORY)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(board)
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void initSimulationFromExistingWithoutAgentsShouldThrowException() {
        RealBoard board = new RealBoard(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);

        AbstractSimulation.BuilderFromExisting builder = new TracingSimulation.BuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessStrategyFactory(DUMMY_AWAKENESS_STRATEGY_FACTORY)
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

        AbstractSimulation.BuilderFromExisting builder = new TracingSimulation.BuilderFromExisting()
                .setId(SIMULATION_ID)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessStrategyFactory(DUMMY_AWAKENESS_STRATEGY_FACTORY)
                .setRadius(RADIUS_EQUALS_ONE)
                .setResolver(resolver)
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void initSimulationFromExistingCurrentRoundBiggerThanMaxRoundShouldThrowException() {
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        Mockito.when(resolver.getBoard()).thenReturn(estimatedBoard);
        ArrayList<Beacon> beacons = new ArrayList<>();
        beacons.add(beacon1);
        beacons.add(beacon2);
        ArrayList<Observer> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        AbstractSimulation.BuilderFromExisting builder = new TracingSimulation.BuilderFromExisting()
                .setId(SIMULATION_ID)
                .setCurrentRound(CURRENT_ROUND_EQUALS_THREE)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessStrategyFactory(DUMMY_AWAKENESS_STRATEGY_FACTORY)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(realBoard)
                .setResolver(resolver)
                .setBeacons(beacons)
                .setObservers(observers);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    // Helper movement strategy.

    private static class moveUp implements IMovementStrategy {

        @Override
        public Location moveTo(IAgentOwner owner, Location currentLocation) {
            Location newLocation = currentLocation.moveInDirection(Direction.UP);
            if (owner.isLocationValid(newLocation)) {
                return newLocation;
            }
            return currentLocation;
        }
    }
}
