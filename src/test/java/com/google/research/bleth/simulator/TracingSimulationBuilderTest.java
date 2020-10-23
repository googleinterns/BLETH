package com.google.research.bleth.simulator;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationBuilderTest {

    private static final IMovementStrategy MOVE_UP = new UpMovementStrategy();
    private static final IMovementStrategy STATIONARY = new StationaryMovementStrategy();

    private static final int BOARD_DIMENSION_EQUALS_TWO = 2;
    private static final int MAX_ROUNDS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_BEACONS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_OBSERVERS_EQUALS_TWO = 2;
    private static final double RADIUS_EQUALS_ONE = 1.0;
    private static final int AWAKENESS_CYCLE_EQUALS_TWO = 2;
    private static final int AWAKENESS_DURATION_EQUALS_ONE = 1;
    private static final int AWAKENESS_DURATION_EQUALS_THREE = 3;
    private static final AwakenessStrategyFactory.Type FIXES_AWAKENESS_STRATEGY_TYPE =
            AwakenessStrategyFactory.Type.FIXED;

    @Test
    public void setIllegalBoardDimensionInBuilderShouldThrowException() {
        final int illegalBoardDimension = 0;
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(illegalBoardDimension)
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
        final int illegalMaxRounds = 0;
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(illegalMaxRounds)
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
        final int illegalRadius = -1;
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setRadius(illegalRadius)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalNumberOfAgentsInBuilderShouldThrowException() {
        final int illegalNumberOfAgents = 0;
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(illegalNumberOfAgents)
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
}
