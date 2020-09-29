package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class BeaconTest {
    private static final Location zeroOnZeroCoordinate = new Location(0, 0);
    private static final Location oneOnOneCoordinate = new Location(1, 1);
    private static final Location zeroOnOneCoordinate = new Location(0, 1);
    private static final Location oneOnZeroCoordinate = new Location(1, 0);

    @Mock
    private Simulation simulation;

    private Beacon createStaticBeaconOnLocation(Location initial_location) {
        return new Beacon(initial_location, new StationaryMovementStrategy(), simulation);
    }

    private Beacon createRandomBeaconOnLocation(Location initial_location) {
        return new Beacon(initial_location, new RandomMovementStrategy(), simulation);
    }

    private int calculateDistance(Location oldLocation, Location newLocation) {
        return Math.abs(newLocation.row - oldLocation.row) + Math.abs(newLocation.col - oldLocation.col);
    }

    private boolean isMovementOfCorneredBeaconIsValid(Board board, Location corner) {
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(corner);

        Location nextLocation = randomBeacon.moveTo();

        return simulation.getBoard().isLocationValid(nextLocation) && calculateDistance(corner, nextLocation) <= 1;
    }

    private boolean movingBeaconLocationIsUpdatedOnBoardMatrix(Board board, Location initialLocation) {
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(initialLocation);

        randomBeacon.move();

        return simulation.getBoard().getAgentsOnLocation(randomBeacon.getLocation()).contains(randomBeacon);
    }

    @Test
    public void createBeaconOutsideTheBoardThrowsException() {
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);

        assertThrows(IllegalArgumentException.class, () -> {
            new Beacon(new Location(0, -1), new RandomMovementStrategy(), simulation);
        });
    }

    @Test
    public void staticBeaconTransmitStaticId() {
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);

        assertThat(beacon.transmit().advertisement).isEqualTo(beacon.id);
    }

    @Test
    public void randomBeaconTransmitStaticId() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(oneOnOneCoordinate);

        assertThat(beacon.transmit().advertisement).isEqualTo(beacon.id);
    }

    @Test
    public void newBeaconLocationIsUpdatedOnBoardMatrix() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnOneCoordinate);

        assertThat(simulation.getBoard().getAgentsOnLocation(zeroOnOneCoordinate)).contains(randomBeacon);
    }

    @Test
    public void newBeaconLocationIsUpdated() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnOneCoordinate);

        assertThat(randomBeacon.getLocation()).isEqualTo(zeroOnOneCoordinate);
    }

    @Test
    public void staticBeaconNextMoveIsToItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon staticBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);

        Location newLocation = staticBeacon.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByFourDirectionsNextMoveIsToItsLocation() {
        // ------
        // | B  |
        // ------
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsNextMoveIsUp() {
        // -----
        // |   |
        // -----
        // | B |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(oneOnZeroCoordinate);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsNextMoveIsDown() {
        // -----
        // | B |
        // -----
        // |   |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(oneOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsNextMoveIsRight() {
        // ----------
        // | B |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnOneCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsNextMoveIsLeft() {
        // ----------
        // |   | B  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnOneCoordinate);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void moveStaticBeaconStayOnItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon staticBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);

        staticBeacon.move();

        assertThat(staticBeacon.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByFourDirectionsStayOnItsLocation(){
        // -----
        // | B |
        // -----
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesUp(){
        // -----
        // |   |
        // -----
        // | B |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(oneOnZeroCoordinate);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesDown(){
        // -----
        // | B |
        // -----
        // |   |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(oneOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesRight(){
        // ----------
        // | B |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(zeroOnOneCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesLeft(){
        // ----------
        // |   | B  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnOneCoordinate);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconMoveExactlyOneStep() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(oneOnOneCoordinate);

        randomBeacon.move();

        assertThat(calculateDistance(randomBeacon.getLocation(), oneOnOneCoordinate)).isEqualTo(1);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMoveExactlyOneStepAside() {
        // -------------
        // | B |   |   |
        // -------------
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnOneCoordinate);

        randomBeacon.move();

        assertThat(calculateDistance(randomBeacon.getLocation(), zeroOnOneCoordinate)).isEqualTo(1);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMoveExactlyOneStepDown() {
        // -----
        // | B |
        // -----
        // |   |
        // -----
        // -----
        // |   |
        // -----
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        randomBeacon.move();

        assertThat(calculateDistance(randomBeacon.getLocation(), zeroOnZeroCoordinate)).isEqualTo(1);
    }

    @Test
    public void upLeftCorneredBeaconsMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredBeaconIsValid(board, zeroOnZeroCoordinate)).isTrue();
        }
    }

    @Test
    public void upRightCorneredBeaconsMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredBeaconIsValid(board, new Location(0, 2))).isTrue();
        }
    }

    @Test
    public void bottomLeftCorneredBeaconsMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredBeaconIsValid(board, new Location(2, 0))).isTrue();
        }
    }

    @Test
    public void bottomRightCorneredBeaconsMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredBeaconIsValid(board, new Location(2, 2))).isTrue();
        }
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesTwiceUpAndDown(){
        // -----
        // |   |
        // -----
        // | B |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(oneOnZeroCoordinate);

        randomBeacon.move();
        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(oneOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesTwiceDownAndUp(){
        // -----
        // | B |
        // -----
        // |   |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        randomBeacon.move();
        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesTwiceRightAndLeft(){
        // ----------
        // | B |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);

        randomBeacon.move();
        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(zeroOnZeroCoordinate);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesTwiceLeftAndRight(){
        // ----------
        // |   | B  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(zeroOnOneCoordinate);

        randomBeacon.move();
        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(zeroOnOneCoordinate);
    }

    @Test
    public void movingBeaconsLocationsAreUpdatedOnBoardMatrix() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(movingBeaconLocationIsUpdatedOnBoardMatrix(board, oneOnOneCoordinate)).isTrue();
        }
    }
}
