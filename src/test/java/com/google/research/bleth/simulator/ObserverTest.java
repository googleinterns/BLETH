package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ObserverTest extends IAgentTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = Location.create(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = Location.create(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = Location.create(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = Location.create(1, 0);
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();
    private static final ObserverFactory OBSERVER_FACTORY = new ObserverFactory();

    private final FakeResolver resolver = new FakeResolver();

    @Test
    public void createObserverOutsideTheBoardThrowsException() {
        RealBoard realBoard = new RealBoard(1, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            OBSERVER_FACTORY.createObserver(Location.create(0, -1), new RandomMovementStrategy(), resolver, realBoard,
                    new FixedAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                               /* awakenessDuration= */ 1,
                                               /* firstAwakenessTime= */ 0));
        });
    }

    @Test
    public void observerDoesNotObserveBeaconsPassesItsRightLocation() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ONE_COORDINATE, realBoard);

        randomObserver.passInformationToResolver();

        assertThat((resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void movingObserverDoesNotObserveBeaconsPassesItsRightLocation() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE, realBoard);
        randomObserver.move();

        randomObserver.passInformationToResolver();

        assertThat((resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerDoesNotObserveBeaconsPassesNoTransmission() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE, realBoard);

        randomObserver.passInformationToResolver();

        assertThat((resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void observerDoesNotObserveNearbyBeaconPassesNoTransmission() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ZERO_COORDINATE, realBoard);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.passInformationToResolver();

        assertThat((resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void observerObservesOneBeaconInSameLocationPassesItsRightLocation() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesOneBeaconInDifferentLocationPassesItsRightLocation() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon newBeacon = createStaticBeaconOnLocation(ONE_ON_ZERO_COORDINATE, realBoard);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void movingObserverObservesOneBeaconPassesItsRightLocation() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission newBeaconTransmission = newBeacon.transmit();
        randomObserver.move();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesOneBeaconInSameLocationPassesTheRightTransmission() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon newBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getTransmissions()).containsExactly(newBeaconTransmission);
    }

    @Test
    public void observerObservesOneBeaconInDifferentLocationPassesTheRightTransmission() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon newBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE, realBoard);
        Transmission newBeaconTransmission = newBeacon.transmit();

        randomObserver.observe(newBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getTransmissions()).containsExactly(newBeaconTransmission);
    }

    @Test
    public void observerObservesOneBeaconOutOfTwoPassesTheRightTransmission() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE, realBoard);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getTransmissions()).containsExactly(firstBeaconTransmission);
    }

    @Test
    public void observerObservesTwoBeaconsPassesItsRightLocation() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ONE_ON_ONE_COORDINATE, realBoard);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getObserverLocation()).isEqualTo(randomObserver.getLocation());
    }

    @Test
    public void observerObservesTwoBeaconsPassesTheRightTransmissions() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getTransmissions()).containsExactly(firstBeaconTransmission, secondBeaconTransmission);
    }

    @Test
    public void transmissionsIsEmptyAfterObserverPassThem() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission firstBeaconTransmission = firstBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();
        randomObserver.passInformationToResolver();

        assertThat((resolver).getTransmissions()).isEmpty();
    }

    @Test
    public void transmissionsRefillAfterObserverPassThem() {
        RealBoard realBoard = new RealBoard(2, 2);
        Observer randomObserver = createRandomObserverOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Beacon firstBeacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realBoard);
        Transmission firstBeaconTransmission = firstBeacon.transmit();
        Beacon secondBeacon = createStaticBeaconOnLocation(ONE_ON_ONE_COORDINATE, realBoard);
        Transmission secondBeaconTransmission = secondBeacon.transmit();

        randomObserver.observe(firstBeaconTransmission);
        randomObserver.passInformationToResolver();
        randomObserver.observe(secondBeaconTransmission);
        randomObserver.passInformationToResolver();

        assertThat((resolver).getTransmissions()).containsExactly(secondBeaconTransmission);
    }

    Observer createRandomAgentOnLocation(Location initialLocation, RealBoard owner) {
        return OBSERVER_FACTORY.createObserver(initialLocation, new RandomMovementStrategy(), resolver, owner,
                new FixedAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                           /* awakenessDuration= */ 1,
                                           /* firstAwakenessTime= */ 0));
    }

    Observer createStaticAgentOnLocation(Location initialLocation, RealBoard owner) {
        return OBSERVER_FACTORY.createObserver(initialLocation, new StationaryMovementStrategy(), resolver, owner,
                new FixedAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                           /* awakenessDuration= */ 1,
                                           /* firstAwakenessTime= */ 0));
    }

    private Observer createRandomObserverOnLocation(Location initialLocation, RealBoard owner) {
        return OBSERVER_FACTORY.createObserver(initialLocation, new RandomMovementStrategy(), resolver, owner,
                new FixedAwakenessStrategy(/* awakenessCycleDuration= */ 5,
                                           /* awakenessDuration= */ 1,
                                           /* firstAwakenessTime= */ 0));
    }

    private Beacon createStaticBeaconOnLocation(Location initial_location, RealBoard owner) {
        return BEACON_FACTORY.createBeacon(initial_location, new StationaryMovementStrategy(), owner);
    }
}