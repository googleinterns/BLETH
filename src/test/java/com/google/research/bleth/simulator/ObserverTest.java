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
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = new Location(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = new Location(1, 0);
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();
    private static final ObserverFactory OBSERVER_FACTORY = new ObserverFactory();

    @Mock
    private Simulation simulation;

    private IResolver resolver = new FakeResolver();

    @Test
    public void createObserverOutsideTheBoardThrowsException() {
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);

        assertThrows(IllegalArgumentException.class, () -> {
            OBSERVER_FACTORY.createObserver(new Location(0, -1), new RandomMovementStrategy(), resolver, simulation,
                    new FixedAwakenessStrategy(5, 1, 0));
        });
    }
    
    @Test
    public void newObserverLocationIsUpdatedOnBoardMatrix() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ONE_COORDINATE);

        assertThat(simulation.getBoard().getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).contains(randomObserver);
    }

    @Test
    public void newObserverLocationIsUpdated() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ONE_COORDINATE);

        assertThat(randomObserver.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void staticObserverNextMoveIsToItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer staticObserver = createStaticObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        Location newLocation = staticObserver.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void moveStaticObserverStayOnItsLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer staticObserver = createStaticObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        staticObserver.move();

        assertThat(staticObserver.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomObserverSurroundedByFourDirectionsStayOnItsLocation() {
        // -----
        // | O |
        // -----
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomObserverMoveExactlyOneStep() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ONE_COORDINATE);

            randomObserver.move();

            assertThat(calculateDistance(randomObserver.getLocation(), ONE_ON_ONE_COORDINATE)).isEqualTo(1);
        }
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMoveExactlyOneStepToTheRight() {
        // -------------
        // | O |   |   |
        // -------------
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomObserver.move();

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(randomObserver);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMoveExactlyOneStepToTheLeft() {
        // -------------
        // |   |   | O |
        // -------------
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(new Location(0, 2));

        randomObserver.move();

        assertThat(board.getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(randomObserver);
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
        Board board = new Board(3, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomObserver.move();

        assertThat(board.getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(randomObserver);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMoveExactlyOneStepUp() {
        // -----
        // |   |
        // -----
        // |   |
        // -----
        // | O |
        // -----
        Board board = new Board(3, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(new Location(2, 0));

        randomObserver.move();

        assertThat(board.getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(randomObserver);
    }

    @Test
    public void upLeftCorneredObserversMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

            Location nextLocation = randomObserver.moveTo();

            assertThat(simulation.getBoard().isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(ZERO_ON_ZERO_COORDINATE, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void upRightCorneredObserversMoveToValidLocations() {
        Location upRightCorner = new Location(0, 2);

        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            Observer randomObserver = createRandomObserverOnLocation(upRightCorner);

            Location nextLocation = randomObserver.moveTo();

            assertThat(simulation.getBoard().isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(upRightCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void bottomLeftCorneredObserversMoveToValidLocations() {
        Location bottomLeftCorner = new Location(2, 0);
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            Observer randomObserver = createRandomObserverOnLocation(bottomLeftCorner);

            Location nextLocation = randomObserver.moveTo();

            assertThat(simulation.getBoard().isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(bottomLeftCorner, nextLocation)).isEqualTo(1);
        }
    }

    @Test
    public void bottomRightCorneredObserversMoveToValidLocations() {
        Location bottomRightCorner = new Location(2, 2);
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            Observer randomObserver = createRandomObserverOnLocation(bottomRightCorner);

            Location nextLocation = randomObserver.moveTo();

            assertThat(simulation.getBoard().isLocationValid(nextLocation)).isTrue();
            assertThat(calculateDistance(bottomRightCorner, nextLocation)).isEqualTo(1);
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
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE);

        randomObserver.move();
        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ONE_ON_ZERO_COORDINATE);
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
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomObserver.move();
        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesTwiceRightAndLeft() {
        // ----------
        // | O |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomObserver.move();
        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesTwiceLeftAndRight() {
        // ----------
        // |   | O  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ONE_COORDINATE);

        randomObserver.move();
        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void movingObserversLocationsAreUpdatedOnBoardMatrix() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            Mockito.when(simulation.getBoard()).thenReturn(board);
            Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ONE_COORDINATE);

            randomObserver.move();

            assertThat(board.getAgentsOnLocation(randomObserver.getLocation())).containsExactly(randomObserver);
        }
    }

    @Test
    public void observerDoesNotObserveBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE);

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void movingObserverDoesNotObserveBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE);
        randomObserver.move();

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerDoesNotObserveBeaconsPassesNoTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE);

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void observerDoesNotObserveNearbyBeaconPassesNoTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void observerObservesOneBeaconInSameLocationPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesOneBeaconInDifferentLocationPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon newBeacon = createStaticBeaconOnLocation(ONE_ON_ZERO_COORDINATE);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void movingObserverObservesOneBeaconPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission newBeaconTransmission = newBeacon.transmit();
        randomObserver.move();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesOneBeaconInSameLocationPassesTheRightTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(newBeaconTransmission);
    }

    @Test
    public void observerObservesOneBeaconInDifferentLocationPassesTheRightTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon newBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(newBeaconTransmission);
    }

    @Test
    public void observerObservesOneBeaconOutOfTwoPassesTheRightTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(firstBeaconTransmission);
    }

    @Test
    public void observerObservesTwoBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ONE_COORDINATE);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesTwoBeaconsPassesTheRightTransmissions() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(firstBeaconTransmission, secondBeaconTransmission);
    }

    @Test
    public void transmissionsIsEmptyAfterObserverPassThem() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission firstBeaconTransmission = firstBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void transmissionsRefillAfterObserverPassThem() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(secondBeaconTransmission);
    }

    private Observer createStaticObserverOnLocation(Location initial_location) {
        return OBSERVER_FACTORY.createObserver(initial_location, new StationaryMovementStrategy(), resolver, simulation,
                new FixedAwakenessStrategy(5, 1, 0));
    }

    private Observer createRandomObserverOnLocation(Location initial_location) {
        return OBSERVER_FACTORY.createObserver(initial_location, new RandomMovementStrategy(), resolver, simulation,
                new FixedAwakenessStrategy(5, 1, 0));
    }

    private Beacon createStaticBeaconOnLocation(Location initial_location) {
        return BEACON_FACTORY.createBeacon(initial_location, new StationaryMovementStrategy(), simulation);
    }

    private int calculateDistance(Location oldLocation, Location newLocation) {
        return Math.abs(newLocation.row - oldLocation.row) + Math.abs(newLocation.col - oldLocation.col);
    }
}
