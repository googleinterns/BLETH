package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class IAgentTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = new Location(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = new Location(1, 0);

    @Mock
    private Simulation simulation;

    @Test
    public void newAgentLocationIsUpdatedOnBoardMatrix() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, simulation);

        assertThat(simulation.getBoard().getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).contains(randomAgent);
    }

    @Test
    public void newAgentLocationIsUpdated() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, simulation);

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void moveStaticAgentStaysOnItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent staticAgent = createStaticAgentOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

        staticAgent.move();

        assertThat(staticAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByFourDirectionsStaysOnItsLocation() {
        // -----
        // | A |
        // -----
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesExactlyOneStepToTheRight() {
        // -------------
        // | A |   |   |
        // -------------
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

        randomAgent.move();

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(randomAgent);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesExactlyOneStepToTheLeft() {
        // -------------
        // |   |   | A |
        // -------------
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(new Location(0, 2), simulation);

        randomAgent.move();

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(randomAgent);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesExactlyOneStepDown() {
        // -----
        // | A |
        // -----
        // |   |
        // -----
        // |   |
        // -----
        Board board = new Board(3, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

        randomAgent.move();

        assertThat(board.getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(randomAgent);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMoveExactlyOneStepUp() {
        // -----
        // |   |
        // -----
        // |   |
        // -----
        // | A |
        // -----
        Board board = new Board(3, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(new Location(2, 0), simulation);

        randomAgent.move();

        assertThat(board.getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(randomAgent);
    }

    @Test
    public void upLeftCorneredAgentsMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

            Location nextLocation = randomAgent.moveTo();

            assertThat(simulation.getBoard().isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(ZERO_ON_ZERO_COORDINATE, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void upRightCorneredAgentsMoveToValidLocations() {
        Location upRightCorner = new Location(0, 2);

        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            IAgent randomAgent = createRandomAgentOnLocation(upRightCorner, simulation);

            Location nextLocation = randomAgent.moveTo();

            assertThat(simulation.getBoard().isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(upRightCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void bottomLeftCorneredAgentsMoveToValidLocations() {
        Location bottomLeftCorner = new Location(2, 0);
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            IAgent randomAgent = createRandomAgentOnLocation(bottomLeftCorner, simulation);

            Location nextLocation = randomAgent.moveTo();

            assertThat(simulation.getBoard().isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(bottomLeftCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void bottomRightCorneredAgentsMoveToValidLocations() {
        Location bottomRightCorner = new Location(2, 2);
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            IAgent randomAgent = createRandomAgentOnLocation(bottomRightCorner, simulation);

            Location nextLocation = randomAgent.moveTo();

            assertThat(simulation.getBoard().isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(bottomRightCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesTwiceUpAndDown() {
        // -----
        // |   |
        // -----
        // | A |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ZERO_COORDINATE, simulation);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ONE_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesTwiceDownAndUp() {
        // -----
        // | A |
        // -----
        // |   |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesTwiceRightAndLeft() {
        // ----------
        // | A |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesTwiceLeftAndRight() {
        // ---------
        // |   | A |
        // ---------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, simulation);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void randomAgentsMoveExactlyOneStep() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ONE_COORDINATE, simulation);

            randomAgent.move();

            assertThat(calculateDistance(randomAgent.getLocation(), ONE_ON_ONE_COORDINATE)).isEqualTo(1);
        }
    }

    @Test
    public void movingAgentsLocationsAreUpdatedOnBoardMatrix() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ONE_COORDINATE, simulation);

            randomAgent.move();

            assertThat(board.getAgentsOnLocation(randomAgent.getLocation())).containsExactly(randomAgent);
        }
    }

    abstract IAgent createRandomAgentOnLocation(Location initialLocation, Simulation simulation);

    abstract IAgent createStaticAgentOnLocation(Location initialLocation, Simulation simulation);

    private int calculateDistance(Location oldLocation, Location newLocation) {
        return Math.abs(newLocation.row - oldLocation.row) + Math.abs(newLocation.col - oldLocation.col);
    }
}