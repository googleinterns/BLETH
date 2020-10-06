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
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = new Location(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = new Location(1, 0);
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();

    @Mock
    private Simulation simulation;

    @Test
    public void createBeaconOutsideTheBoardThrowsException() {
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);

        assertThrows(IllegalArgumentException.class, () -> {
            BEACON_FACTORY.createBeacon(new Location(0, -1), new RandomIMovementStrategy(), simulation);
        });
    }

    @Test
    public void staticBeaconTransmitStaticId() {
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        assertThat(beacon.transmit().advertisement).isEqualTo(beacon.getId());
    }

    @Test
    public void randomBeaconTransmitStaticId() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE);

        assertThat(beacon.transmit().advertisement).isEqualTo(beacon.getId());
    }

    @Test
    public void newBeaconLocationIsUpdatedOnBoardMatrix() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE);

        assertThat(simulation.getBoard().getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).contains(randomBeacon);
    }

    @Test
    public void newBeaconLocationIsUpdated() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE);

        assertThat(randomBeacon.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void staticBeaconNextMoveIsToItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon staticBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        Location newLocation = staticBeacon.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomBeaconSurroundedByFourDirectionsNextMoveIsToItsLocation() {
        // ------
        // | B  |
        // ------
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ZERO_COORDINATE);
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
        Beacon randomBeacon = createRandomBeaconOnLocation(ONE_ON_ZERO_COORDINATE);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ZERO_COORDINATE);
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
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(ONE_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsNextMoveIsRight() {
        // ----------
        // | B |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsNextMoveIsLeft() {
        // ----------
        // |   | B  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE);

        Location newLocation = randomBeacon.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void moveStaticBeaconStayOnItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon staticBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        staticBeacon.move();

        assertThat(staticBeacon.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomBeaconSurroundedByFourDirectionsStayOnItsLocation(){
        // -----
        // | B |
        // -----
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
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
        Beacon randomBeacon = createRandomBeaconOnLocation(ONE_ON_ZERO_COORDINATE);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
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
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ONE_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesRight(){
        // ----------
        // | B |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesLeft(){
        // ----------
        // |   | B  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE);

        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomBeaconMoveExactlyOneStep() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE);

        randomBeacon.move();

        assertThat(calculateDistance(randomBeacon.getLocation(), ONE_ON_ONE_COORDINATE)).isEqualTo(1);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMoveExactlyOneStepAside() {
        // -------------
        // | B |   |   |
        // -------------
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE);

        randomBeacon.move();

        assertThat(calculateDistance(randomBeacon.getLocation(), ZERO_ON_ONE_COORDINATE)).isEqualTo(1);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMoveExactlyOneStepDown() {
        // -----
        // | B |
        // -----
        // |   |
        // -----
        // |   |
        // -----
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomBeacon.move();

        assertThat(calculateDistance(randomBeacon.getLocation(), ZERO_ON_ZERO_COORDINATE)).isEqualTo(1);
    }

    @Test
    public void upLeftCorneredBeaconsMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredBeaconIsValid(board, ZERO_ON_ZERO_COORDINATE)).isTrue();
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
        Beacon randomBeacon = createRandomBeaconOnLocation(ONE_ON_ZERO_COORDINATE);

        randomBeacon.move();
        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ONE_ON_ZERO_COORDINATE);
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
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomBeacon.move();
        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesTwiceRightAndLeft(){
        // ----------
        // | B |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomBeacon.move();
        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomBeaconSurroundedByThreeDirectionsMovesTwiceLeftAndRight(){
        // ----------
        // |   | B  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon randomBeacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE);

        randomBeacon.move();
        randomBeacon.move();

        assertThat(randomBeacon.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void movingBeaconsLocationsAreUpdatedOnBoardMatrix() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(movingBeaconLocationIsUpdatedOnBoardMatrix(board, ONE_ON_ONE_COORDINATE)).isTrue();
        }
    }

    private Beacon createStaticBeaconOnLocation(Location initial_location) {
        return BEACON_FACTORY.createBeacon(initial_location, new StationaryIMovementStrategy(), simulation);
    }

    private Beacon createRandomBeaconOnLocation(Location initial_location) {
        return BEACON_FACTORY.createBeacon(initial_location, new RandomIMovementStrategy(), simulation);
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
}
