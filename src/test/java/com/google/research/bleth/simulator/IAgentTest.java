package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public abstract class IAgentTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = Location.create(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = Location.create(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = Location.create(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = Location.create(1, 0);

    @Test
    public void newAgentLocationIsUpdatedOnBoardMatrix() {
        RealBoard realBoard = new RealBoard(2, 2);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, realBoard);

        assertThat(realBoard.agentsOnBoard()).containsExactly(ZERO_ON_ONE_COORDINATE, randomAgent);
    }

    @Test
    public void newAgentLocationIsUpdated() {
        RealBoard realBoard = new RealBoard(2, 2);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, realBoard);

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void moveStaticAgentStaysOnItsLocation() {
        RealBoard realBoard = new RealBoard(2, 2);
        IAgent staticAgent = createStaticAgentOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);

        staticAgent.move();

        assertThat(staticAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByFourDirectionsStaysOnItsLocation() {
        // -----
        // | A |
        // -----
        RealBoard realBoard = new RealBoard(1, 1);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);

        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesExactlyOneStepToTheRight() {
        // -------------
        // | A |   |   |
        // -------------
        RealBoard realBoard = new RealBoard(1, 3);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);

        randomAgent.move();

        assertThat(realBoard.agentsOnBoard()).containsExactly(ZERO_ON_ONE_COORDINATE, randomAgent);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesExactlyOneStepToTheLeft() {
        // -------------
        // |   |   | A |
        // -------------
        RealBoard realBoard = new RealBoard(1, 3);
        IAgent randomAgent = createRandomAgentOnLocation(Location.create(0, 2), realBoard);

        randomAgent.move();

        assertThat(realBoard.agentsOnBoard()).containsExactly(ZERO_ON_ONE_COORDINATE, randomAgent);
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
        RealBoard realBoard = new RealBoard(3, 1);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);

        randomAgent.move();

        assertThat(realBoard.agentsOnBoard()).containsExactly(ONE_ON_ZERO_COORDINATE, randomAgent);
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
        RealBoard realBoard = new RealBoard(3, 1);
        IAgent randomAgent = createRandomAgentOnLocation(Location.create(2, 0), realBoard);

        randomAgent.move();

        assertThat(realBoard.agentsOnBoard()).containsExactly(ONE_ON_ZERO_COORDINATE, randomAgent);
    }

    @Test
    public void upLeftCorneredAgentsMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            RealBoard realBoard = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);

            Location nextLocation = randomAgent.moveTo();

            assertThat(realBoard.isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(ZERO_ON_ZERO_COORDINATE, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void upRightCorneredAgentsMoveToValidLocations() {
        Location upRightCorner = Location.create(0, 2);

        for (int i = 0; i < 1000; i++) {
            RealBoard realBoard = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(upRightCorner, realBoard);

            Location nextLocation = randomAgent.moveTo();

            assertThat(realBoard.isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(upRightCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void bottomLeftCorneredAgentsMoveToValidLocations() {
        Location bottomLeftCorner = Location.create(2, 0);
        for (int i = 0; i < 1000; i++) {
            RealBoard realBoard = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(bottomLeftCorner, realBoard);

            Location nextLocation = randomAgent.moveTo();

            assertThat(realBoard.isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(bottomLeftCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void bottomRightCorneredAgentsMoveToValidLocations() {
        Location bottomRightCorner = Location.create(2, 2);
        for (int i = 0; i < 1000; i++) {
            RealBoard realBoard = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(bottomRightCorner, realBoard);

            Location nextLocation = randomAgent.moveTo();

            assertThat(realBoard.isLocationValid(nextLocation)).isTrue();
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
        RealBoard realBoard = new RealBoard(2, 1);
        IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ZERO_COORDINATE, realBoard);

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
        RealBoard realBoard = new RealBoard(2, 1);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesTwiceRightAndLeft() {
        // ----------
        // | A |    |
        // ----------
        RealBoard realBoard = new RealBoard(1, 2);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomAgentSurroundedByThreeDirectionsMovesTwiceLeftAndRight() {
        // ---------
        // |   | A |
        // ---------
        RealBoard realBoard = new RealBoard(1, 2);
        IAgent randomAgent = createRandomAgentOnLocation(ZERO_ON_ONE_COORDINATE, realBoard);

        randomAgent.move();
        randomAgent.move();

        assertThat(randomAgent.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void randomAgentsMoveExactlyOneStep() {
        for (int i = 0; i < 1000; i++) {
            RealBoard realBoard = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ONE_COORDINATE, realBoard);

            randomAgent.move();

            assertThat(calculateDistance(randomAgent.getLocation(), ONE_ON_ONE_COORDINATE)).isEqualTo(1);
        }
    }

    @Test
    public void movingAgentsLocationsAreUpdatedOnBoardMatrix() {
        for (int i = 0; i < 1000; i++) {
            RealBoard realBoard = new RealBoard(3, 3);
            IAgent randomAgent = createRandomAgentOnLocation(ONE_ON_ONE_COORDINATE, realBoard);

            randomAgent.move();

            assertThat(realBoard.agentsOnBoard()).containsExactly(randomAgent.getLocation(), randomAgent);
        }
    }

    abstract IAgent createRandomAgentOnLocation(Location initialLocation, RealBoard owner);

    abstract IAgent createStaticAgentOnLocation(Location initialLocation, RealBoard owner);

    private int calculateDistance(Location oldLocation, Location newLocation) {
        return Math.abs(newLocation.row() - oldLocation.row()) + Math.abs(newLocation.col() - oldLocation.col());
    }
}