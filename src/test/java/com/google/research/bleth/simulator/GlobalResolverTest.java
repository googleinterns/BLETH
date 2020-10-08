package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class GlobalResolverTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final Location ONE_ON_ONE_COORDINATE = new Location(1, 1);
    private static final Location ZERO_ON_ONE_COORDINATE = new Location(0, 1);
    private static final Location ONE_ON_ZERO_COORDINATE = new Location(1, 0);
    private static final Location TWO_ON_TWO_COORDINATE = new Location(2, 2);
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();

    @Mock
    private Simulation simulation;

    @Test
    public void estimatedBoardHasTheSameDimensionsAsRealBoard() {
        Board board = new Board(2, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of());

        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        assertThat(resolver.getBoard().getRowsNum()).isEqualTo(2);
        assertThat(resolver.getBoard().getColsNum()).isEqualTo(3);
    }

    @Test
    public void estimatedBoardInitializedEmpty() {
        Board board = new Board(2, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));

        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        assertThat(isBoardEmpty(resolver.getBoard())).isTrue();
    }

    @Test
    public void firstEstimateWithoutReceiveInformation_EstimatedBoardIsEmpty() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.estimate();

        assertThat(isBoardEmpty(resolver.getBoard()));
    }

    @Test
    public void firstEstimateWithoutTransmissions_EstimatedBoardIsEmpty() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of());
        resolver.estimate();

        assertThat(isBoardEmpty(resolver.getBoard()));
    }

    @Test
    public void firstEstimateWithInformationFromObserverOnSameLocationAsBeacon_UpdateEstimatedBoardAccordingToItsLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).isEmpty();
    }

    @Test
    public void firstEstimateWithInformationFromObserverOnDifferentLocationThanBeacon_UpdateEstimatedBoardAccordingToItsLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(ONE_ON_ONE_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).containsExactly(beacon);
    }

    @Test
    public void firstEstimateOfTwoBeaconsWithInformationFromOneObserver_UpdateEstimatedBoardAccordingToItsLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon firstBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon secondBeacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(firstBeacon, secondBeacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(ONE_ON_ONE_COORDINATE, ImmutableList.of(firstBeacon.transmit(), secondBeacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).containsExactly(firstBeacon, secondBeacon);
    }

    @Test
    public void firstEstimateWithInformationFromTwoObserversObservedOtherBeacons_UpdateEstimatedBoardAccordingToTheirLocations() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon firstBeacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Beacon secondBeacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(firstBeacon, secondBeacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(ZERO_ON_ONE_COORDINATE, ImmutableList.of(firstBeacon.transmit()));
        resolver.receiveInformation(ONE_ON_ONE_COORDINATE, ImmutableList.of(secondBeacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(firstBeacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).containsExactly(secondBeacon);
    }

    @Test
    public void firstEstimateWithInformationFromTwoObserversObservedSameBeacon_UpdateEstimatedBoardAccordingToAverageOfLocations() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(TWO_ON_TWO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(TWO_ON_TWO_COORDINATE)).isEmpty();
    }

    @Test
    public void secondEstimateWithInformationFromStationaryObserver_UpdateEstimatedBoardAccordingToAverageOfItsLocationAndPreviousEstimatedLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ONE_ON_ONE_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).isEmpty();
    }

    @Test
    public void secondEstimateWithInformationFromMovingObserver_UpdateEstimatedBoardAccordingToAverageOfItsLocationAndPreviousEstimatedLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(TWO_ON_TWO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(TWO_ON_TWO_COORDINATE)).isEmpty();
    }

    @Test
    public void secondEstimateWithInformationFromTwoObservers_UpdateEstimatedBoardAccordingToAverageOfTheirLocationsAndPreviousLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ONE_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(ONE_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(new Location(2, 1), ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ZERO_COORDINATE)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ZERO_COORDINATE)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(TWO_ON_TWO_COORDINATE)).isEmpty();
    }

    @Test
    public void secondEstimateWithoutInformation_DoNotUpdateTheBoard() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(ZERO_ON_ONE_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ZERO_ON_ONE_COORDINATE)).containsExactly(beacon);
    }

    @Test
    public void secondEstimateForUnlocatedBeacon_UpdateAccordingToTheObserverLocationOnly() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(ONE_ON_ONE_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(ONE_ON_ONE_COORDINATE)).containsExactly(beacon);
    }

    @Test
    public void transmissionsFromPreviousRoundsIsDeleted() {
        Board board = new Board(5, 5);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ONE_ON_ZERO_COORDINATE);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(ZERO_ON_ZERO_COORDINATE, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(new Location(4, 4), ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(TWO_ON_TWO_COORDINATE)).containsExactly(beacon);
    }

    private Beacon createRandomBeaconOnLocation(Location initial_location) {
        return BEACON_FACTORY.createBeacon(initial_location, new RandomMovementStrategy(), simulation);
    }

    private boolean isBoardEmpty(Board board) {
        for (int row = 0; row < board.getRowsNum(); row++) {
            for (int col = 0; col < board.getColsNum(); col++) {
                if (!board.getAgentsOnLocation(new Location(row, col)).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}
