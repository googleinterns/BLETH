package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class IAgentTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = new Location(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = new Location(1, 0);

    @Test
    public void newAgentLocationIsUpdatedOnBoardMatrix() {
        Board board = new RealBoard(2, 2);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, board);

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).contains(randomAgent);
    }

    @Test
    public void newAgentLocationIsUpdated() {
        Board board = new RealBoard(2, 2);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, board);

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void moveStaticAgentStaysOnItsLocation() {
        Board board = new RealBoard(2, 2);
        IAgent staticAgent = createStaticAgentOnLocation(ZERO_ON_ZERO_COORDINATE, board);

        staticAgent.move();

        assertThat(staticAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByFourDirectionsStaysOnItsLocation() {
        // -----
        // | A |
        // -----
        Board board = new RealBoard(1, 1);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, board);

        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesExactlyOneStepToTheRight() {
        // -------------
        // | A |   |   |
        // -------------
        Board board = new RealBoard(1, 3);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, board);

        randomAgent.move();

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(randomAgent);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesExactlyOneStepToTheLeft() {
        // -------------
        // |   |   | A |
        // -------------
        Board board = new RealBoard(1, 3);
        IAgent randomAgent = createRandomAgentOnLocation(new Location(0, 2), board);

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
        Board board = new RealBoard(3, 1);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, board);

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
        Board board = new RealBoard(3, 1);
        IAgent randomAgent = createRandomAgentOnLocation(new Location(2, 0), board);

        randomAgent.move();

        assertThat(board.getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(randomAgent);
    }

    @Test
    public void upLeftCorneredAgentsMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, board);

            Location nextLocation = randomAgent.moveTo();

            assertThat(board.isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(ZERO_ON_ZERO_COORDINATE, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void upRightCorneredAgentsMoveToValidLocations() {
        Location upRightCorner = new Location(0, 2);

        for (int i = 0; i < 1000; i++) {
            Board board = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(upRightCorner, board);

            Location nextLocation = randomAgent.moveTo();

            assertThat(board.isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(upRightCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void bottomLeftCorneredAgentsMoveToValidLocations() {
        Location bottomLeftCorner = new Location(2, 0);
        for (int i = 0; i < 1000; i++) {
            Board board = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(bottomLeftCorner, board);

            Location nextLocation = randomAgent.moveTo();

            assertThat(board.isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(bottomLeftCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void bottomRightCorneredAgentsMoveToValidLocations() {
        Location bottomRightCorner = new Location(2, 2);
        for (int i = 0; i < 1000; i++) {
            Board board = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(bottomRightCorner, board);

            Location nextLocation = randomAgent.moveTo();

            assertThat(board.isLocationValid(nextLocation)).isTrue();
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
        Board board = new RealBoard(2, 1);
        IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ZERO_COORDINATE, board);

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
        Board board = new RealBoard(2, 1);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, board);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesTwiceRightAndLeft() {
        // ----------
        // | A |    |
        // ----------
        Board board = new RealBoard(1, 2);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, board);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesTwiceLeftAndRight() {
        // ---------
        // |   | A |
        // ---------
        Board board = new RealBoard(1, 2);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, board);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void randomAgentsMoveExactlyOneStep() {
        for (int i = 0; i < 1000; i++) {
            Board board = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ONE_COORDINATE, board);

            randomAgent.move();

            assertThat(calculateDistance(randomAgent.getLocation(), ONE_ON_ONE_COORDINATE)).isEqualTo(1);
        }
    }

    @Test
    public void movingAgentsLocationsAreUpdatedOnBoardMatrix() {
        for (int i = 0; i < 1000; i++) {
            Board board = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ONE_COORDINATE, board);

            randomAgent.move();

            assertThat(board.getAgentsOnLocation(randomAgent.getLocation())).containsExactly(randomAgent);
        }
    }

    abstract IAgent createRandomAgentOnLocation(Location initialLocation, Board owner);

    abstract IAgent createStaticAgentOnLocation(Location initialLocation, Board owner);

    private int calculateDistance(Location oldLocation, Location newLocation) {
        return Math.abs(newLocation.row - oldLocation.row) + Math.abs(newLocation.col - oldLocation.col);
    }
}