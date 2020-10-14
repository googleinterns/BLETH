package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BeaconTest extends IAgentTest {
    private static final Location ZERO_ON_ZERO_COORDINATE = new Location(0, 0);
    private static final BeaconFactory BEACON_FACTORY = new BeaconFactory();

    @Test
    public void createBeaconOutsideTheBoardThrowsException() {
        Board board = new Board(1, 1);

        assertThrows(IllegalArgumentException.class, () -> {
            BEACON_FACTORY.createBeacon(new Location(0, -1), new RandomMovementStrategy(), board);
        });
    }

    @Test
    public void staticBeaconTransmitStaticId() {
        Board board = new Board(1, 1);
        Beacon beacon = createStaticBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);

        assertThat(beacon.transmit().advertisement).isEqualTo(beacon.getId());
    }

    @Test
    public void randomBeaconTransmitStaticId() {
        Board board = new Board(3, 3);
        Beacon beacon = createRandomBeaconOnLocation(ZERO_ON_ZERO_COORDINATE, board);

        assertThat(beacon.transmit().advertisement).isEqualTo(beacon.getId());
    }

    Beacon createRandomAgentOnLocation(Location initialLocation, Board owner) {
        return createRandomBeaconOnLocation(initialLocation, owner);
    }

    Beacon createStaticAgentOnLocation(Location initialLocation, Board owner) {
        return createStaticBeaconOnLocation(initialLocation, owner);
    }

    private Beacon createStaticBeaconOnLocation(Location initialLocation, Board owner) {
        return BEACON_FACTORY.createBeacon(initialLocation, new StationaryMovementStrategy(), owner);
    }

    private Beacon createRandomBeaconOnLocation(Location initialLocation, Board owner) {
        return BEACON_FACTORY.createBeacon(initialLocation, new RandomMovementStrategy(), owner);
    }
}