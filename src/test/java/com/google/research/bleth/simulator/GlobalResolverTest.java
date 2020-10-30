package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GlobalResolverTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = new Location(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = new Location(1, 0);
    private static final Location TWO_ON_TWO_COORDINATE = new Location(2, 2);
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();

    @Test
    public void estimatedBoardInitializedEmpty() {
        RealBoard realboard = new RealBoard(2, 2);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);

        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 2,
                                                        /* colsNum= */ 2,
                                                        ImmutableList.of(beacon));

        assertThat(resolver.getBoard().agentsOnBoard()).isEmpty();
    }

    @Test
    public void firstEstimateWithoutReceiveInformation_estimatedBoardIsEmpty() {
        RealBoard realboard = new RealBoard(2, 2);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 2,
                                                        /* colsNum= */ 2,
                                                        ImmutableList.of(beacon));

        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).isEmpty();
    }

    @Test
    public void firstEstimateAfterReceivingZeroTransmissions_estimatedBoardIsEmpty() {
        RealBoard realboard = new RealBoard(2, 2);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 2,
                                                        /* colsNum= */ 2,
                                                        ImmutableList.of(beacon));

        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of());
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).isEmpty();
    }

    @Test
    public void firstEstimateWithInformationFromObserverOnSameLocationAsBeacon_updateEstimatedBoardAccordingToItsLocation() {
        RealBoard realboard = new RealBoard(2, 2);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 2,
                                                        /* colsNum= */ 2,
                                                        ImmutableList.of(beacon));

        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(ZERO_ON_ZERO_COORDINATE, beacon);
    }

    @Test
    public void firstEstimateWithInformationFromObserverOnDifferentLocationThanBeacon_updateEstimatedBoardAccordingToItsLocation() {
        RealBoard realboard = new RealBoard(2, 2);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 2,
                                                        /* colsNum= */ 2,
                                                        ImmutableList.of(beacon));

        resolver.receiveInformation(ONE_ON_ONE_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(ONE_ON_ONE_COORDINATE, beacon);
    }

    @Test
    public void firstEstimateOfTwoBeaconsWithInformationFromOneObserver_updateEstimatedBoardAccordingToItsLocation() {
        RealBoard realboard = new RealBoard(2, 2);
        Beacon firstBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        Beacon secondBeacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 2,
                                                        /* colsNum= */ 2,
                                                        ImmutableList.of(firstBeacon, secondBeacon));

        resolver.receiveInformation(ONE_ON_ONE_COORDINATE, ImmutableList.of(firstBeacon.transmit(), secondBeacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard())
                .containsExactly(ONE_ON_ONE_COORDINATE, firstBeacon,
                                 ONE_ON_ONE_COORDINATE, secondBeacon);
    }

    @Test
    public void firstEstimateWithInformationFromTwoObserversObservedOtherBeacons_updateEstimatedBoardAccordingToTheirLocations() {
        RealBoard realboard = new RealBoard(2, 2);
        Beacon firstBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        Beacon secondBeacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 2,
                                                        /* colsNum= */ 2,
                                                        ImmutableList.of(firstBeacon, secondBeacon));

        resolver.receiveInformation(ZERO_ON_ONE_COORDINATE, ImmutableList.of(firstBeacon.transmit()));
        resolver.receiveInformation(ONE_ON_ONE_COORDINATE, ImmutableList.of(secondBeacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard())
                .containsExactly(ZERO_ON_ONE_COORDINATE, firstBeacon,
                                 ONE_ON_ONE_COORDINATE, secondBeacon);
    }

    @Test
    public void firstEstimateWithInformationFromTwoObserversObservedSameBeacon_updateEstimatedBoardAccordingToAverageOfLocations() {
        RealBoard realboard = new RealBoard(3, 3);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 3,
                                                        /* colsNum= */ 3,
                                                        ImmutableList.of(beacon));

        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(TWO_ON_TWO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(ONE_ON_ONE_COORDINATE, beacon);
    }

    @Test
    public void secondEstimateWithInformationFromStationaryObserver_updateEstimatedBoardAccordingToAverageOfItsLocationAndPreviousEstimatedLocation() {
        RealBoard realboard = new RealBoard(3, 3);
        Beacon beacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 3,
                                                        /* colsNum= */ 3,
                                                        ImmutableList.of(beacon));
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(ZERO_ON_ZERO_COORDINATE, beacon);
    }

    @Test
    public void secondEstimateWithInformationFromAnotherObserver_updateEstimatedBoardAccordingToAverageOfItsLocationAndPreviousEstimatedLocation() {
        RealBoard realboard = new RealBoard(3, 3);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 3,
                                                        /* colsNum= */ 3,
                                                        ImmutableList.of(beacon));
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(TWO_ON_TWO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(ONE_ON_ONE_COORDINATE, beacon);
    }

    @Test
    public void secondEstimateWithInformationFromTwoObservers_updateEstimatedBoardAccordingToAverageOfTheirLocationsAndPreviousLocation() {
        RealBoard realboard = new RealBoard(3, 3);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 3,
                                                        /* colsNum= */ 3,
                                                        ImmutableList.of(beacon));
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(ONE_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(new Location(2, 1), ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(ONE_ON_ZERO_COORDINATE, beacon);
    }

    @Test
    public void secondEstimateWithoutInformation_doNotUpdateTheBoard() {
        RealBoard realboard = new RealBoard(3, 3);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 3,
                                                        /* colsNum= */ 3,
                                                        ImmutableList.of(beacon));
        resolver.receiveInformation(TWO_ON_TWO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(TWO_ON_TWO_COORDINATE, beacon);
    }

    @Test
    public void secondEstimateForUnlocatedBeacon_updateAccordingToTheObserverLocationOnly() {
        RealBoard realboard = new RealBoard(3, 3);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 3,
                                                        /* colsNum= */ 3,
                                                        ImmutableList.of(beacon));
        resolver.estimate();

        resolver.receiveInformation(TWO_ON_TWO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(TWO_ON_TWO_COORDINATE, beacon);
    }

    @Test
    public void transmissionsFromPreviousRoundsIsDeleted() {
        RealBoard realboard = new RealBoard(5, 5);
        Beacon beacon = createRandomBeaconOnLocation(ONE_ON_ZERO_COORDINATE, realboard);
        GlobalResolver resolver = GlobalResolver.create(/* rowsNum= */ 5,
                                                        /* colsNum= */ 5,
                                                        ImmutableList.of(beacon));
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(new Location(4, 4), ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().agentsOnBoard()).containsExactly(TWO_ON_TWO_COORDINATE, beacon);
    }

    private Beacon createRandomBeaconOnLocation(Location initialLocation, RealBoard realBoard) {
        return BEACON_FACTORY.createBeacon(initialLocation, new RandomMovementStrategy(), realBoard);
    }
}
