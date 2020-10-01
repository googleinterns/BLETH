package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ObserverTest {
    private static final Location zeroOnZeroCoordinate = new Location(0, 0);
    private static final Location oneOnOneCoordinate = new Location(1, 1);
    private static final Location zeroOnOneCoordinate = new Location(0, 1);
    private static final Location oneOnZeroCoordinate = new Location(1, 0);
    private static final BeaconFactory beaconFactory = new BeaconFactory();
    private static final ObserverFactory observerFactory = new ObserverFactory();

    @Mock
    private Simulation simulation;

    /**
     * A fake implementation for IResolver that allows to test the arguments it receives when an observer calls receiveInformation.
     */
    private class FakeResolver implements IResolver {
        private Location observerLocation;
        private List<Transmission> transmissions;

        @Override
        public void receiveInformation(Location observerLocation, List<Transmission> transmissions) {
            this.observerLocation = observerLocation;
            this.transmissions = transmissions;
        }

        public Location getObserverLocation() {
            return observerLocation;
        }

        public List<Transmission> getTransmissions() {
            return transmissions;
        }

        @Override
        public Board getBoard() {
            return null;
        }
    }

    private IResolver resolver = new FakeResolver();

    private Observer createStaticObserverOnLocation(Location initial_location) {
        return observerFactory.createObserver(initial_location, new StationaryMovementStrategy(), resolver, simulation,
        1, 1, new FixedAwakenessStrategy());
    }

    private Observer createRandomObserverOnLocation(Location initial_location) {
        return observerFactory.createObserver(initial_location, new RandomMovementStrategy(), resolver, simulation,
                1, 1, new FixedAwakenessStrategy());
    }

    private Observer createObserverByAwakenessTimes(int awakenessDuration, int firstAwakenessTime, AwakenessStrategy awakenessStrategy) {
        return observerFactory.createObserver(zeroOnZeroCoordinate, new RandomMovementStrategy(), resolver, simulation,
                awakenessDuration, firstAwakenessTime, awakenessStrategy);
    }

    private Beacon createStaticBeaconOnLocation(Location initial_location) {
        return beaconFactory.createBeacon(initial_location, new StationaryMovementStrategy(), simulation);
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
            observerFactory.createObserver(new Location(0, -1), new RandomMovementStrategy(), resolver, simulation,
                    1, 0, new FixedAwakenessStrategy());
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

    @Test
    public void observerDoesNotObserveBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnZeroCoordinate);

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void movingObserverDoesNotObserveBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnZeroCoordinate);
        randomObserver.move();

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerDoesNotObserveBeaconsPassesNoTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnZeroCoordinate);

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void observerDoesNotObserveNearbyBeaconPassesNoTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnZeroCoordinate);
        Beacon newBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void observerObservesOneBeaconInSameLocationPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);
        Beacon newBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesOneBeaconInDifferentLocationPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);
        Beacon newBeacon = createStaticBeaconOnLocation(oneOnZeroCoordinate);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void movingObserverObservesOneBeaconPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);
        Beacon newBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
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
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);
        Beacon newBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(newBeaconTransmission);
    }

    @Test
    public void observerObservesOneBeaconInDifferentLocationPassesTheRightTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);
        Beacon newBeacon = createStaticBeaconOnLocation(oneOnOneCoordinate);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(newBeaconTransmission);
    }

    @Test
    public void observerObservesOneBeaconOutOfTwoPassesTheRightTransmission() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);
        Beacon firstBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(oneOnOneCoordinate);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(firstBeaconTransmission);
    }

    @Test
    public void observerObservesTwoBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(oneOnOneCoordinate);
        Beacon firstBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
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
        Observer randomObserver = createRandomObserverOnLocation(zeroOnZeroCoordinate);
        Beacon firstBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(zeroOnZeroCoordinate);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(firstBeaconTransmission, secondBeaconTransmission);
    }

    @Test
    public void fixedAwakenessObserverWithFirstAwakenessTimeZeroStartsAwake() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(3, 0, new FixedAwakenessStrategy());

        assertThat(fixedObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void randomAwakenessObserverWithFirstAwakenessTimeZeroStartsAwake() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(3, 0, new RandomAwakenessStrategy());

        assertThat(randomObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverWithFirstAwakenessTimeOneStartsAsleep() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(3, 1, new FixedAwakenessStrategy());

        assertThat(fixedObserver.isObserverAwake()).isFalse();
    }

    @Test
    public void randomAwakenessObserverWithFirstAwakenessTimeOneStartsAsleep() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(3, 1, new RandomAwakenessStrategy());

        assertThat(randomObserver.isObserverAwake()).isFalse();
    }

    @Test
    public void fixedAwakenessObserverWakesUpAtRoundTwo() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(3, 2, new FixedAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);

        fixedObserver.updateAwakenessState(2);

        assertThat(fixedObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void randomAwakenessObserverWakesUpAtRoundTwo() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(3, 2, new RandomAwakenessStrategy());
        randomObserver.updateAwakenessState(1);

        randomObserver.updateAwakenessState(2);

        assertThat(randomObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundTwoSleepsAtRoundOne() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(3, 2, new FixedAwakenessStrategy());

        fixedObserver.updateAwakenessState(1);

        assertThat(fixedObserver.isObserverAwake()).isFalse();
    }

    @Test
    public void randomAwakenessObserverThatWakesUpAtRoundTwoSleepsAtRoundOne() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(3, 2, new RandomAwakenessStrategy());

        randomObserver.updateAwakenessState(1);

        assertThat(randomObserver.isObserverAwake()).isFalse();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundTwoForTwoRoundsIsAwakeAtRoundThree() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(2, 2, new FixedAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);
        fixedObserver.updateAwakenessState(2);

        fixedObserver.updateAwakenessState(3);

        assertThat(fixedObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void randomAwakenessObserverThatWakesUpAtRoundTwoForTwoRoundsIsAwakeAtRoundThree() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(2, 2, new RandomAwakenessStrategy());
        randomObserver.updateAwakenessState(1);
        randomObserver.updateAwakenessState(2);

        randomObserver.updateAwakenessState(3);

        assertThat(randomObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundOneForTwoRoundsAsleepAtRoundThree() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(10);
        Observer fixedObserver = createObserverByAwakenessTimes(2, 1, new FixedAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);
        fixedObserver.updateAwakenessState(2);

        fixedObserver.updateAwakenessState(3);

        assertThat(fixedObserver.isObserverAwake()).isFalse();
    }

    @Test
    public void randomAwakenessObserverThatWakesUpAtRoundOneForTwoRoundsAsleepAtRoundThree() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(10);
        Observer randomObserver = createObserverByAwakenessTimes(2, 1, new RandomAwakenessStrategy());
        randomObserver.updateAwakenessState(1);
        randomObserver.updateAwakenessState(2);

        randomObserver.updateAwakenessState(3);

        assertThat(randomObserver.isObserverAwake()).isFalse();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundOneWakesUpExactlyOneCycleLater() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer fixedObserver = createObserverByAwakenessTimes(1, 1, new FixedAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);
        fixedObserver.updateAwakenessState(2);

        fixedObserver.updateAwakenessState(3);

        assertThat(fixedObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundZeroAndIsAwakeForAWholeCycleStaysAwake() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer fixedObserver = createObserverByAwakenessTimes(2, 0, new FixedAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);
        fixedObserver.updateAwakenessState(2);

        assertThat(fixedObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void randomAwakenessObserverThatWakesUpAtRoundZeroAndIsAwakeForAWholeCycleStaysAwake() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer randomObserver = createObserverByAwakenessTimes(2, 0, new RandomAwakenessStrategy());
        randomObserver.updateAwakenessState(1);
        randomObserver.updateAwakenessState(2);

        assertThat(randomObserver.isObserverAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverWakesUpAtTheSecondCycle() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer fixedObserver = createObserverByAwakenessTimes(1, 0, new FixedAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);

        fixedObserver.updateAwakenessState(2);
        boolean wakesUpAtTwo = fixedObserver.isObserverAwake();
        fixedObserver.updateAwakenessState(3);
        boolean wakesUpAtThree = fixedObserver.isObserverAwake();

        assertThat(wakesUpAtTwo || wakesUpAtThree).isTrue();
    }

    @Test
    public void randomAwakenessObserverWakesUpAtTheSecondCycle() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer randomObserver = createObserverByAwakenessTimes(1, 0, new RandomAwakenessStrategy());
        randomObserver.updateAwakenessState(1);

        randomObserver.updateAwakenessState(2);
        boolean wakesUpAtTwo = randomObserver.isObserverAwake();
        randomObserver.updateAwakenessState(3);
        boolean wakesUpAtThree = randomObserver.isObserverAwake();

        assertThat(wakesUpAtTwo || wakesUpAtThree).isTrue();
    }

    @Test
    public void fixedAwakenessObserverWakesUpExactlyOnceAtTheSecondCycle() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer fixedObserver = createObserverByAwakenessTimes(1, 0, new FixedAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);

        fixedObserver.updateAwakenessState(2);
        boolean wakesUpAtTwo = fixedObserver.isObserverAwake();
        fixedObserver.updateAwakenessState(3);
        boolean wakesUpAtThree = fixedObserver.isObserverAwake();

        assertThat(wakesUpAtTwo && wakesUpAtThree).isFalse();
    }

    @Test
    public void randomAwakenessObserverWakesUpExactlyOnceAtTheSecondCycle() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer randomObserver = createObserverByAwakenessTimes(1, 0, new RandomAwakenessStrategy());
        randomObserver.updateAwakenessState(1);

        randomObserver.updateAwakenessState(2);
        boolean wakesUpAtTwo = randomObserver.isObserverAwake();
        randomObserver.updateAwakenessState(3);
        boolean wakesUpAtThree = randomObserver.isObserverAwake();

        assertThat(wakesUpAtTwo && wakesUpAtThree).isFalse();
    }
}
