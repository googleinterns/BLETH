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
            OBSERVER_FACTORY.createObserver(new Location(0, -1), new RandomIMovementStrategy(), resolver, simulation,
                    1, 0, new FixedIAwakenessStrategy());
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
    public void randomObserverSurroundedByFourDirectionsNextMoveIsToItsLocation() {
        // ------
        // | O  |
        // ------
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ZERO_COORDINATE);
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
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ZERO_COORDINATE);
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
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(ONE_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsNextMoveIsRight() {
        // ----------
        // | O |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        Location newLocation = randomObserver.moveTo();

        assertThat(newLocation).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsNextMoveIsLeft() {
        // ----------
        // |   | O  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ONE_COORDINATE);

        Location newLocation = randomObserver.moveTo();

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
    public void randomObserverSurroundedByThreeDirectionsMovesUp() {
        // -----
        // |   |
        // -----
        // | O |
        // -----
        Board board = new Board(2, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
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
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ONE_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesRight() {
        // ----------
        // | O |    |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ZERO_ON_ONE_COORDINATE);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMovesLeft() {
        // ----------
        // |   | O  |
        // ----------
        Board board = new Board(1, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ONE_COORDINATE);

        randomObserver.move();

        assertThat(randomObserver.getLocation()).isEqualTo(ZERO_ON_ZERO_COORDINATE);
    }

    @Test
    public void randomObserverMoveExactlyOneStep() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ONE_COORDINATE);

        randomObserver.move();

        assertThat(calculateDistance(randomObserver.getLocation(), ONE_ON_ONE_COORDINATE)).isEqualTo(1);
    }

    @Test
    public void randomObserverSurroundedByThreeDirectionsMoveExactlyOneStepAside() {
        // -------------
        // | O |   |   |
        // -------------
        Board board = new Board(1, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ONE_COORDINATE);

        randomObserver.move();

        assertThat(calculateDistance(randomObserver.getLocation(), ZERO_ON_ONE_COORDINATE)).isEqualTo(1);
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
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE);

        randomObserver.move();

        assertThat(calculateDistance(randomObserver.getLocation(), ZERO_ON_ZERO_COORDINATE)).isEqualTo(1);
    }

    @Test
    public void upLeftCorneredObserversMoveToValidLocations() {
        for (int i = 0; i < 1000; i++) {
            Board board = new Board(3, 3);
            assertThat(isMovementOfCorneredObserverIsValid(board, ZERO_ON_ZERO_COORDINATE)).isTrue();
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
            assertThat(movingObserverLocationIsUpdatedOnBoardMatrix(board, ONE_ON_ONE_COORDINATE)).isTrue();
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

    @Test
    public void fixedAwakenessObserverWithFirstAwakenessTimeZeroStartsAwake() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(3, 0, new FixedIAwakenessStrategy());

        assertThat(fixedObserver.isAwake()).isTrue();
    }

    @Test
    public void randomAwakenessObserverWithFirstAwakenessTimeZeroStartsAwake() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(3, 0, new RandomIAwakenessStrategy());

        assertThat(randomObserver.isAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverWithFirstAwakenessTimeOneStartsAsleep() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(3, 1, new FixedIAwakenessStrategy());

        assertThat(fixedObserver.isAwake()).isFalse();
    }

    @Test
    public void randomAwakenessObserverWithFirstAwakenessTimeOneStartsAsleep() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(3, 1, new RandomIAwakenessStrategy());

        assertThat(randomObserver.isAwake()).isFalse();
    }

    @Test
    public void fixedAwakenessObserverWakesUpAtRoundTwo() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(3, 2, new FixedIAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);

        fixedObserver.updateAwakenessState(2);

        assertThat(fixedObserver.isAwake()).isTrue();
    }

    @Test
    public void randomAwakenessObserverWakesUpAtRoundTwo() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(3, 2, new RandomIAwakenessStrategy());
        randomObserver.updateAwakenessState(1);

        randomObserver.updateAwakenessState(2);

        assertThat(randomObserver.isAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundTwoSleepsAtRoundOne() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(3, 2, new FixedIAwakenessStrategy());

        fixedObserver.updateAwakenessState(1);

        assertThat(fixedObserver.isAwake()).isFalse();
    }

    @Test
    public void randomAwakenessObserverThatWakesUpAtRoundTwoSleepsAtRoundOne() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(3, 2, new RandomIAwakenessStrategy());

        randomObserver.updateAwakenessState(1);

        assertThat(randomObserver.isAwake()).isFalse();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundTwoForTwoRoundsIsAwakeAtRoundThree() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer fixedObserver = createObserverByAwakenessTimes(2, 2, new FixedIAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);
        fixedObserver.updateAwakenessState(2);

        fixedObserver.updateAwakenessState(3);

        assertThat(fixedObserver.isAwake()).isTrue();
    }

    @Test
    public void randomAwakenessObserverThatWakesUpAtRoundTwoForTwoRoundsIsAwakeAtRoundThree() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Observer randomObserver = createObserverByAwakenessTimes(2, 2, new RandomIAwakenessStrategy());
        randomObserver.updateAwakenessState(1);
        randomObserver.updateAwakenessState(2);

        randomObserver.updateAwakenessState(3);

        assertThat(randomObserver.isAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundOneForTwoRoundsAsleepAtRoundThree() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(10);
        Observer fixedObserver = createObserverByAwakenessTimes(2, 1, new FixedIAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);
        fixedObserver.updateAwakenessState(2);

        fixedObserver.updateAwakenessState(3);

        assertThat(fixedObserver.isAwake()).isFalse();
    }

    @Test
    public void randomAwakenessObserverThatWakesUpAtRoundOneForTwoRoundsAsleepAtRoundThree() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(10);
        Observer randomObserver = createObserverByAwakenessTimes(2, 1, new RandomIAwakenessStrategy());
        randomObserver.updateAwakenessState(1);
        randomObserver.updateAwakenessState(2);

        randomObserver.updateAwakenessState(3);

        assertThat(randomObserver.isAwake()).isFalse();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundOneWakesUpExactlyOneCycleLater() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer fixedObserver = createObserverByAwakenessTimes(1, 1, new FixedIAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);
        fixedObserver.updateAwakenessState(2);

        fixedObserver.updateAwakenessState(3);

        assertThat(fixedObserver.isAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverThatWakesUpAtRoundZeroAndIsAwakeForAWholeCycleStaysAwake() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer fixedObserver = createObserverByAwakenessTimes(2, 0, new FixedIAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);
        fixedObserver.updateAwakenessState(2);

        assertThat(fixedObserver.isAwake()).isTrue();
    }

    @Test
    public void randomAwakenessObserverThatWakesUpAtRoundZeroAndIsAwakeForAWholeCycleStaysAwake() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer randomObserver = createObserverByAwakenessTimes(2, 0, new RandomIAwakenessStrategy());
        randomObserver.updateAwakenessState(1);
        randomObserver.updateAwakenessState(2);

        assertThat(randomObserver.isAwake()).isTrue();
    }

    @Test
    public void fixedAwakenessObserverWakesUpAtTheSecondCycle() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer fixedObserver = createObserverByAwakenessTimes(1, 0, new FixedIAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);

        fixedObserver.updateAwakenessState(2);
        boolean wakesUpAtTwo = fixedObserver.isAwake();
        fixedObserver.updateAwakenessState(3);
        boolean wakesUpAtThree = fixedObserver.isAwake();

        assertThat(wakesUpAtTwo || wakesUpAtThree).isTrue();
    }

    @Test
    public void randomAwakenessObserverWakesUpAtTheSecondCycle() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer randomObserver = createObserverByAwakenessTimes(1, 0, new RandomIAwakenessStrategy());
        randomObserver.updateAwakenessState(1);

        randomObserver.updateAwakenessState(2);
        boolean wakesUpAtTwo = randomObserver.isAwake();
        randomObserver.updateAwakenessState(3);
        boolean wakesUpAtThree = randomObserver.isAwake();

        assertThat(wakesUpAtTwo || wakesUpAtThree).isTrue();
    }

    @Test
    public void fixedAwakenessObserverWakesUpExactlyOnceAtTheSecondCycle() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer fixedObserver = createObserverByAwakenessTimes(1, 0, new FixedIAwakenessStrategy());
        fixedObserver.updateAwakenessState(1);

        fixedObserver.updateAwakenessState(2);
        boolean wakesUpAtTwo = fixedObserver.isAwake();
        fixedObserver.updateAwakenessState(3);
        boolean wakesUpAtThree = fixedObserver.isAwake();

        assertThat(wakesUpAtTwo && wakesUpAtThree).isFalse();
    }

    @Test
    public void randomAwakenessObserverWakesUpExactlyOnceAtTheSecondCycle() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        Observer randomObserver = createObserverByAwakenessTimes(1, 0, new RandomIAwakenessStrategy());
        randomObserver.updateAwakenessState(1);

        randomObserver.updateAwakenessState(2);
        boolean wakesUpAtTwo = randomObserver.isAwake();
        randomObserver.updateAwakenessState(3);
        boolean wakesUpAtThree = randomObserver.isAwake();

        assertThat(wakesUpAtTwo && wakesUpAtThree).isFalse();
    }

    @Test
    public void fixedAwakenessTimeIsFixed() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        boolean wakesUpAtTwo = false;
        boolean wakesUpAtThree = false;

        for (int i = 0; i < 1000; i++) {
            Observer fixedObserver = createObserverByAwakenessTimes(1, 0, new FixedIAwakenessStrategy());
            fixedObserver.updateAwakenessState(1);

            fixedObserver.updateAwakenessState(2);
            if (fixedObserver.isAwake()) {
                wakesUpAtTwo = true;
            }
            fixedObserver.updateAwakenessState(3);
            if (fixedObserver.isAwake()) {
                wakesUpAtThree = true;
            }
        }

        assertThat(wakesUpAtTwo && !wakesUpAtThree).isTrue();
    }

    @Test
    public void randomAwakenessTimeIsNotFixed() {
        Board board = new Board(2, 2);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getAwakenessCycle()).thenReturn(2);
        boolean wakesUpAtTwo = false;
        boolean wakesUpAtThree = false;

        for (int i = 0; i < 1000; i++) {
            Observer randomObserver = createObserverByAwakenessTimes(1, 0, new RandomIAwakenessStrategy());
            randomObserver.updateAwakenessState(1);

            randomObserver.updateAwakenessState(2);
            if (randomObserver.isAwake()) {
                wakesUpAtTwo = true;
            }
            randomObserver.updateAwakenessState(3);
            if (randomObserver.isAwake()) {
                wakesUpAtThree = true;
            }
        }

        assertThat(wakesUpAtTwo && wakesUpAtThree).isTrue();
    }

    private Observer createStaticObserverOnLocation(Location initial_location) {
        return OBSERVER_FACTORY.createObserver(initial_location, new StationaryIMovementStrategy(), resolver, simulation,
                1, 1, new FixedIAwakenessStrategy());
    }

    private Observer createRandomObserverOnLocation(Location initial_location) {
        return OBSERVER_FACTORY.createObserver(initial_location, new RandomIMovementStrategy(), resolver, simulation,
                1, 1, new FixedIAwakenessStrategy());
    }

    private Observer createObserverByAwakenessTimes(int awakenessDuration, int firstAwakenessTime, IAwakenessStrategy IAwakenessStrategy) {
        return OBSERVER_FACTORY.createObserver(ZERO_ON_ZERO_COORDINATE, new RandomIMovementStrategy(), resolver, simulation,
                awakenessDuration, firstAwakenessTime, IAwakenessStrategy);
    }

    private Beacon createStaticBeaconOnLocation(Location initial_location) {
        return BEACON_FACTORY.createBeacon(initial_location, new StationaryIMovementStrategy(), simulation);
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
}
