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

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationBuilderTest {

    private static final MovementStrategyFactory.Type MOVE_UP = MovementStrategyFactory.Type.UP;
    private static final MovementStrategyFactory.Type STATIONARY = MovementStrategyFactory.Type.STATIONARY;

    private static final int BOARD_DIMENSION_EQUALS_TWO = 2;
    private static final int MAX_ROUNDS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_BEACONS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_OBSERVERS_EQUALS_TWO = 2;
    private static final double TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE = 1.0;
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalRadiusInBuilderShouldThrowException() {
        final int illegalTransmissionThresholdRadius = -1;
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setTransmissionThresholdRadius(illegalTransmissionThresholdRadius)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setObserverMovementStrategyType(STATIONARY)
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setObserverMovementStrategyType(STATIONARY);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingBoardDimensionInBuilderShouldThrowException() {
        AbstractSimulation.Builder builder = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_TWO)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_TWO)
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
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
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
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
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE);

        assertThrows(IllegalArgumentException.class, builder::build);
    }
}
