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
    private static final Location zeroOnZeroCoordinate = new Location(0, 0);
    private static final Location oneOnOneCoordinate = new Location(1, 1);
    private static final Location zeroOnOneCoordinate = new Location(0, 1);
    private static final Location oneOnZeroCoordinate = new Location(1, 0);
    private static final Location twoOnTwoCoordinate = new Location(2, 2);
    private static final BeaconFactory beaconFactory = new BeaconFactory();

    @Mock
    private Simulation simulation;

    private Beacon createRandomBeaconOnLocation(Location initial_location) {
        return beaconFactory.createBeacon(initial_location, new RandomMovementStrategy(), simulation);
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
        Beacon beacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));

        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        assertThat(isBoardEmpty(resolver.getBoard())).isTrue();
    }

    @Test
    public void firstEstimateWithoutReceiveInformation_DoesNotUpdateEstimatedBoard() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.estimate();

        assertThat(isBoardEmpty(resolver.getBoard()));
    }

    @Test
    public void firstEstimateWithoutInformation_DoesNotUpdateEstimatedBoard() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(zeroOnZeroCoordinate, ImmutableList.of());
        resolver.estimate();

        assertThat(isBoardEmpty(resolver.getBoard()));
    }

    @Test
    public void firstEstimateWithInformationFromOneObserverOnSameLocationAsBeacon_UpdateEstimatedBoardAccordingToItsLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(oneOnOneCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(zeroOnZeroCoordinate, ImmutableList.of(beacon.transmit()));

        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnOneCoordinate)).isEmpty();
    }

    @Test
    public void firstEstimateWithInformationFromOneObserverOnDifferentLocationThanBeacon_UpdateEstimatedBoardAccordingToItsLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(oneOnOneCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(beacon);
    }

    @Test
    public void firstEstimateOfTwoBeaconsWithInformationFromOneObserver_UpdateEstimatedBoardAccordingToItsLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon firstBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Beacon secondBeacon = createRandomBeaconOnLocation(oneOnOneCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(firstBeacon, secondBeacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(oneOnOneCoordinate, ImmutableList.of(firstBeacon.transmit(), secondBeacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(firstBeacon, secondBeacon);
    }

    @Test
    public void firstEstimateWithInformationFromTwoObserversObservedOtherBeacons_UpdateEstimatedBoardAccordingToTheirLocations() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon firstBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Beacon secondBeacon = createRandomBeaconOnLocation(oneOnOneCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(firstBeacon, secondBeacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(zeroOnOneCoordinate, ImmutableList.of(firstBeacon.transmit()));
        resolver.receiveInformation(oneOnOneCoordinate, ImmutableList.of(secondBeacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstBeacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(secondBeacon);
    }

    @Test
    public void firstEstimateWithInformationFromTwoObserversObservedSameBeacon_UpdateEstimatedBoardAccordingToAverageOfLocations() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);

        resolver.receiveInformation(zeroOnZeroCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(twoOnTwoCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(twoOnTwoCoordinate)).isEmpty();
    }

    @Test
    public void secondEstimateWithInformationFromStationaryObserver_UpdateEstimatedBoardAccordingToAverageOfItsLocationAndPreviousEstimatedLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(oneOnOneCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(zeroOnZeroCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(zeroOnZeroCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnZeroCoordinate)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnOneCoordinate)).isEmpty();
    }

    @Test
    public void secondEstimateWithInformationFromMovingObserver_UpdateEstimatedBoardAccordingToAverageOfItsLocationAndPreviousEstimatedLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(zeroOnOneCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(zeroOnZeroCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(twoOnTwoCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(twoOnTwoCoordinate)).isEmpty();
    }

    @Test
    public void secondEstimateWithInformationFromTwoObservers_UpdateEstimatedBoardAccordingToAverageOfTheirLocationsAndPreviousLocation() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(zeroOnOneCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(zeroOnZeroCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(oneOnZeroCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.receiveInformation(new Location(2, 1), ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnZeroCoordinate)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnOneCoordinate)).isEmpty();
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnZeroCoordinate)).containsExactly(beacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(twoOnTwoCoordinate)).isEmpty();
    }

    @Test
    public void secondEstimateWithoutInformation_DoNotUpdateTheBoard() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(beacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(zeroOnOneCoordinate, ImmutableList.of(beacon.transmit()));
        resolver.estimate();

        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(beacon);
    }

    @Test
    public void secondEstimateForUnlocatedBeacon_UpdateAccordingToTheNewLocationsOnly() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon firstBeacon = createRandomBeaconOnLocation(zeroOnZeroCoordinate);
        Beacon secondBeacon = createRandomBeaconOnLocation(oneOnOneCoordinate);
        Mockito.when(simulation.getBeacons()).thenReturn(ImmutableList.of(firstBeacon, secondBeacon));
        GlobalResolver resolver = GlobalResolver.createResolver(simulation);
        resolver.receiveInformation(zeroOnOneCoordinate, ImmutableList.of(firstBeacon.transmit()));
        resolver.estimate();

        resolver.receiveInformation(oneOnOneCoordinate, ImmutableList.of(secondBeacon.transmit()));
        resolver.estimate();

        assertThat(resolver.getBoard().getAgentsOnLocation(zeroOnOneCoordinate)).containsExactly(firstBeacon);
        assertThat(resolver.getBoard().getAgentsOnLocation(oneOnOneCoordinate)).containsExactly(secondBeacon);
    }
}
