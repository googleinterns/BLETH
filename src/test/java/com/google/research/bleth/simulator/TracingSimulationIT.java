package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationIT {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SCATTERED));

    private static final IMovementStrategy MOVE_UP = new UpMovementStrategy();
    private static final IMovementStrategy STATIONARY = new StationaryMovementStrategy();

    private static final int BOARD_DIMENSION_EQUALS_TWO = 2;
    private static final int MAX_ROUNDS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_BEACONS_EQUALS_ONE = 1;
    private static final int NUMBER_OF_OBSERVERS_EQUALS_ONE = 1;
    private static final double TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE = 1.0;
    private static final int AWAKENESS_CYCLE_EQUALS_TWO = 2;
    private static final int AWAKENESS_DURATION_EQUALS_ONE = 1;
    private static final AwakenessStrategyFactory.Type FIXES_AWAKENESS_STRATEGY_TYPE = AwakenessStrategyFactory.Type.FIXED;

    @Before
    public void setUp() {
        helper.setUp();
    }

    @Test
    public void runSimulationSingleRoundVerifyAgentsLocations() {
        // Create new simulation.
        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_TWO)
                .setRowNum(BOARD_DIMENSION_EQUALS_TWO)
                .setColNum(BOARD_DIMENSION_EQUALS_TWO)
                .setBeaconsNum(NUMBER_OF_BEACONS_EQUALS_ONE)
                .setObserversNum(NUMBER_OF_OBSERVERS_EQUALS_ONE)
                .setTransmissionThresholdRadius(TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE)
                .setBeaconMovementStrategy(MOVE_UP)
                .setObserverMovementStrategy(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE)
                .build();

        // Find agents and calculate their expected location after a single round.
        Location beaconInitialLocation = simulation.beacons.get(0).getLocation();
        Location observerInitialLocation = simulation.observers.get(0).getLocation();
        Location beaconExpectedLocation = predictLocationAfterMoveUp(beaconInitialLocation, 1);
        Location observerExpectedLocation = observerInitialLocation;

        // Run single-rounded simulation and find agents again.
        simulation.run();
        Location beaconActualLocation = simulation.beacons.get(0).getLocation();
        Location observerActualLocation = simulation.observers.get(0).getLocation();

        assertThat(beaconActualLocation).isEqualTo(beaconExpectedLocation);
        assertThat(observerActualLocation).isEqualTo(observerExpectedLocation);
    }

    private Location predictLocationAfterMoveUp(Location location, int numberOfRounds) {
        return Location.create(Math.max(0, location.row() - numberOfRounds), location.col());
    }
}
