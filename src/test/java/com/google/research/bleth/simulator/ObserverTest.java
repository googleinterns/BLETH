package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ObserverTest extends IAgentTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = new Location(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = new Location(1, 0);
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();
    private static final ObserverFactory OBSERVER_FACTORY = new ObserverFactory();

    private final IResolver resolver = new FakeResolver();

    @Test
    public void createObserverOutsideTheBoardThrowsException() {
        Board board = new Board(1, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            OBSERVER_FACTORY.createObserver(new Location(0, -1), new RandomMovementStrategy(), resolver, board,
                    new FixedAwakenessStrategy(5, 1, 0));
        });
    }

    @Test
    public void observerDoesNotObserveBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ONE_COORDINATE, board);

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void movingObserverDoesNotObserveBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE, board);
        randomObserver.move();

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerDoesNotObserveBeaconsPassesNoTransmission() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE, board);

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void observerDoesNotObserveNearbyBeaconPassesNoTransmission() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE, board);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void observerObservesOneBeaconInSameLocationPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesOneBeaconInDifferentLocationPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon newBeacon = createStaticBeaconOnLocation(ONE_ON_ZERO_COORDINATE, board);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void movingObserverObservesOneBeaconPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission newBeaconTransmission = newBeacon.transmit();
        randomObserver.move();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesOneBeaconInSameLocationPassesTheRightTransmission() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(newBeaconTransmission);
    }

    @Test
    public void observerObservesOneBeaconInDifferentLocationPassesTheRightTransmission() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon newBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE, board);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(newBeaconTransmission);
    }

    @Test
    public void observerObservesOneBeaconOutOfTwoPassesTheRightTransmission() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE, board);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(firstBeaconTransmission);
    }

    @Test
    public void observerObservesTwoBeaconsPassesItsRightLocation() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ONE_COORDINATE, board);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesTwoBeaconsPassesTheRightTransmissions() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(firstBeaconTransmission, secondBeaconTransmission);
    }

    @Test
    public void transmissionsIsEmptyAfterObserverPassThem() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission firstBeaconTransmission = firstBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void transmissionsRefillAfterObserverPassThem() {
        Board board = new Board(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE, board);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat(((FakeResolver) resolver).getTransmissions()).containsExactly(secondBeaconTransmission);
    }

    Observer createRandomAgentOnLocation(Location initialLocation, Board owner) {
        return OBSERVER_FACTORY.createObserver(initialLocation, new RandomMovementStrategy(), resolver, owner,
                new FixedAwakenessStrategy(5, 1, 0));    }

    Observer createStaticAgentOnLocation(Location initialLocation, Board owner) {
        return OBSERVER_FACTORY.createObserver(initialLocation, new StationaryMovementStrategy(), resolver, owner,
                new FixedAwakenessStrategy(5, 1, 0));    }

    private Observer createRandomObserverOnLocation(Location initialLocation, Board owner) {
        return OBSERVER_FACTORY.createObserver(initialLocation, new RandomMovementStrategy(), resolver, owner,
                new FixedAwakenessStrategy(5, 1, 0));
    }

    private Beacon createStaticBeaconOnLocation(Location initial_location, Board owner) {
        return BEACON_FACTORY.createBeacon(initial_location, new StationaryMovementStrategy(), owner);
    }
}