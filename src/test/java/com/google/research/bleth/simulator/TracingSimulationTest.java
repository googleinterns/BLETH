package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.Test;

public class TracingSimulationTest {

    public final MovementStrategy moveUp = new moveUp();
    public final MovementStrategy stationary = new StationaryMovementStrategy();

    private static final String simulationId = "test-sim-id-1";
    private static final int maxRoundsEqualsTwo = 2;
    private static final int numberOfBeaconsEqualsTwo = 2;
    private static final int numberOfObserversEqualsTwo = 2;

    private static final int awakenessCycleEqualsTwo = 2;
    private static final int awakenessDurationEqualsOne = 1;
    private static final double radiusEqualsOne = 1.0;

    private static final int illegalBoardDimension = 0;
    private static final int illegalNumberOfAgents = 0;
    private static final int illegalMaxRounds = 0;
    private static final int illegalRadius = -1;

    public class moveUp implements MovementStrategy {

        @Override
        public Location moveTo(Board board, Location currentLocation) {
            Location newLocation = currentLocation.moveInDirection(Direction.UP);
            if (board.isLocationValid(newLocation)) {
                return newLocation;
            }
            return currentLocation;
        }
    }

    public Board buildExpectedBoard(Board originalBoard, int rowNum, int colNum) {

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

    public boolean boardsEqual(Board actual, Board expected, int rowNum, int colNum) {
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

    @Test
    public void initializeTwoBeaconsBeaconsContainerShouldEqualTwo() {
        Simulation simulation = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary)
                .build();

        simulation.initializeBeacons();

        assertThat(simulation.getBeacons().size()).isEqualTo(2);
    }

    @Test
    public void runSimulationWithMaxRoundNumberTwoCurrentRoundNumberShouldBeTwo() {
        Simulation simulation = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary)
                .build();

        simulation.run();

        assertThat(simulation.currentRound).isEqualTo(maxRoundsEqualsTwo);
    }

    @Test
    public void moveAgentsDeterministicMovementStrategyBoardShouldEqualExpectedBoard() {
        Simulation simulation = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary)
                .build();

        Board boardWhenSimulationInitialized = simulation.getBoard();
        Board expectedBoardAfterSingleRound = buildExpectedBoard(boardWhenSimulationInitialized, 2, 2);

        simulation.moveAgents();
        Board boardAfterMovement = simulation.getBoard();

        assertThat(boardsEqual(boardAfterMovement, expectedBoardAfterSingleRound, 2, 2)).isTrue();
    }

    @Test
    public void setIllegalBoardDimensionInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(illegalBoardDimension)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalMaxRoundsInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(illegalMaxRounds).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalRadiusInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(illegalRadius).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void setIllegalNumberOfAgentsInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(illegalNumberOfAgents)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary);

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingBeaconMovementStrategyInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setObserverMovementStrategy(stationary);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingObserverMovementStrategyInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp);

        assertThrows(NullPointerException.class, builder::build);
    }

    @Test
    public void skipSettingBoardDimensionInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary);;

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingNumberOfAgentsInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary);;

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingMaxRoundsNumberInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setRadius(radiusEqualsOne).setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary);;

        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void skipSettingRadiusInBuilderShouldThrowException() {
        Simulation.SimulationBuilder builder = new TracingSimulation.TracingSimulationBuilder()
                .setId(simulationId).setMaxNumberOfRounds(maxRoundsEqualsTwo).setRowNum(2).setColNum(2)
                .setBeaconsNum(numberOfBeaconsEqualsTwo).setObserversNum(numberOfObserversEqualsTwo)
                .setAwakenessCycle(awakenessCycleEqualsTwo).setAwakenessDuration(awakenessDurationEqualsOne)
                .setBeaconMovementStrategy(moveUp).setObserverMovementStrategy(stationary);;

        assertThrows(IllegalArgumentException.class, builder::build);
    }
}
