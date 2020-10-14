package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.Mock;

@RunWith(MockitoJUnitRunner.class)
public class BeaconTest extends IAgentTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();

    @Mock
    private Simulation simulation;

    @Test
    public void createBeaconOutsideTheBoardThrowsException() {
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);

        assertThrows(IllegalArgumentException.class, () -> {
            BEACON_FACTORY.createBeacon(new Location(0, -1), new RandomMovementStrategy(), simulation);
        });
    }

    @Test
    public void staticBeaconTransmitStaticId() {
        Board board = new Board(1, 1);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

        assertThat(beacon.transmit().advertisement).isEqualTo(beacon.getId());
    }

    @Test
    public void randomBeaconTransmitStaticId() {
        Board board = new Board(3, 3);
        Mockito.when(simulation.getBoard()).thenReturn(board);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, simulation);

        assertThat(beacon.transmit().advertisement).isEqualTo(beacon.getId());
    }

    Beacon createRandomAgentOnLocation(Location initialLocation, Simulation simulation) {
        return createRandomBeaconOnLocation(initialLocation, simulation);
    }

    Beacon createStaticAgentOnLocation(Location initialLocation, Simulation simulation) {
        return createStaticBeaconOnLocation(initialLocation, simulation);
    }

    private Beacon createStaticBeaconOnLocation(Location initialLocation, Simulation simulation) {
        return BEACON_FACTORY.createBeacon(initialLocation, new StationaryMovementStrategy(), simulation);
    }

    private Beacon createRandomBeaconOnLocation(Location initialLocation, Simulation simulation) {
        return BEACON_FACTORY.createBeacon(initialLocation, new RandomMovementStrategy(), simulation);
    }
}