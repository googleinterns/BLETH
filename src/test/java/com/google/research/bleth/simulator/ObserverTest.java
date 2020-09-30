package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class ObserverTest {
    private static final Location zeroOnZeroCoordinate = new Location(0, 0);
    private static final Location oneOnOneCoordinate = new Location(1, 1);
    private static final Location zeroOnOneCoordinate = new Location(0, 1);
    private static final Location oneOnZeroCoordinate = new Location(1, 0);
    private static final ObserverFactory observerFactory = new ObserverFactory();

    @Mock
    private Simulation simulation;

    @Mock
    private IResolver resolver;

    private Observer createStaticObserverOnLocation(Location initial_location) {
        return observerFactory.createObserver(initial_location, new StationaryMovementStrategy(), resolver, simulation);
    }

    private Observer createRandomObserverOnLocation(Location initial_location) {
        return observerFactory.createObserver(initial_location, new RandomMovementStrategy(), resolver, simulation);
    }

    private int calculateDistance(Location oldLocation, Location newLocation) {
        return Math.abs(newLocation.row - oldLocation.row) + Math.abs(newLocation.col - oldLocation.col);
    }

    private boolean isMovementOfCorneredObserverIsValid(Board board, Location corner) {
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(corner);

        Location nextLocation = randomObserver.moveTo();

        return simulation.getBoard().isLocationValid(nextLocation) && calculateDistance(corner, nextLocation) <= 1;
    }

    private boolean movingObserverLocationIsUpdatedOnBoardMatrix(Board board, Location initialLocation) {
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(initialLocation);

        randomObserver.move();

        return simulation.getBoard().getAgentsOnLocation(randomObserver.getLocation()).contains(randomObserver);
    }

    @Test
    public void createObserverOutsideTheBoardThrowsException() {
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);

        assertThrows(IllegalArgumentException.class, () -> {
            observerFactory.createObserver(new Location(0, -1), new RandomMovementStrategy(), resolver, simulation);
        });
    }
    
    @Test
    public void newObserverLocationIsUpdatedOnBoardMatrix() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnOneCoordinate);

        assertThat(simulation.getBoard().getAgentsOnLocation(zeroOnOneCoordinate)).contains(randomObserver);
    }

    @Test
    public void newObserverLocationIsUpdated() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnOneCoordinate);

        assertThat(randomObserver.getLocation()).isEqualTo(zeroOnOneCoordinate);
    }

    @Test
    public void staticObserverNextMoveIsToItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer staticObserver = createStaticObserverOnLocation(zeroOnZeroCoordinate);

        Location newLocation = staticObserver.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByFourDirectionsNextMoveIsToItsLocation() {
        // ------
        // | O  |
        // ------
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsNextMoveIsUp() {
        // -----
        // |   |
        // -----
        // | O |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnZeroCoordinate);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsNextMoveIsDown() {
        // -----
        // | O |
        // -----
        // |   |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(oneOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsNextMoveIsRight() {
        // ----------
        // | O |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnOneCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsNextMoveIsLeft() {
        // ----------
        // |   | O  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnOneCoordinate);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void moveStaticObserverStayOnItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer staticObserver = createStaticObserverOnLocation(zeroOnZeroCoordinate);

        staticObserver.move();

        assertThat(staticObserver.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByFourDirectionsStayOnItsLocation() {
        // -----
        // | O |
        // -----
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesUp() {
        // -----
        // |   |
        // -----
        // | O |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnZeroCoordinate);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesDown() {
        // -----
        // | O |
        // -----
        // |   |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(oneOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesRight() {
        // ----------
        // | O |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(zeroOnOneCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesLeft() {
        // ----------
        // |   | O  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnOneCoordinate);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverMoveExactlyOneStep() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnOneCoordinate);

        randomObserver.move();

        assertThat(calculateDistance(randomObserver.getLocation(), oneOnOneCoordinate)).isEqualTo(1);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMoveExactlyOneStepAside() {
        // -------------
        // | O |   |   |
        // -------------
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnOneCoordinate);

        randomObserver.move();

        assertThat(calculateDistance(randomObserver.getLocation(), zeroOnOneCoordinate)).isEqualTo(1);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMoveExactlyOneStepDown() {
        // -----
        // | O |
        // -----
        // |   |
        // -----
        // |   |
        // -----
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        randomObserver.move();

        assertThat(calculateDistance(randomObserver.getLocation(), zeroOnZeroCoordinate)).isEqualTo(1);
    }

    @Test
    public void upLeftCorneredObserversMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredObserverIsValid(board, zeroOnZeroCoordinate)).isTrue();
        }
    }

    @Test
    public void upRightCorneredObserversMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredObserverIsValid(board, new Location(0, 2))).isTrue();
        }
    }

    @Test
    public void bottomLeftCorneredObserversMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredObserverIsValid(board, new Location(2, 0))).isTrue();
        }
    }

    @Test
    public void bottomRightCorneredObserversMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredObserverIsValid(board, new Location(2, 2))).isTrue();
        }
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesTwiceUpAndDown() {
        // -----
        // |   |
        // -----
        // | O |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnZeroCoordinate);

        randomObserver.move();
        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(oneOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesTwiceDownAndUp() {
        // -----
        // | O |
        // -----
        // |   |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        randomObserver.move();
        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesTwiceRightAndLeft() {
        // ----------
        // | O |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);

        randomObserver.move();
        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesTwiceLeftAndRight() {
        // ----------
        // |   | O  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnOneCoordinate);

        randomObserver.move();
        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(zeroOnOneCoordinate);
    }

    @Test
    public void movingObserversLocationsAreUpdatedOnBoardMatrix() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(movingObserverLocationIsUpdatedOnBoardMatrix(board, oneOnOneCoordinate)).isTrue();
        }
    }
}
