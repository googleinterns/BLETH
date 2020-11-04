package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.Multimap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationIT {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SCATTERED));

    private static final MovementStrategyFactory.Type MOVE_UP = MovementStrategyFactory.Type.UP;
    private static final MovementStrategyFactory.Type STATIONARY = MovementStrategyFactory.Type.STATIONARY;

    private static final int BOARD_DIMENSION_EQUALS_TWO = 2;
    private static final int MAX_ROUNDS_EQUALS_TWO = 2;
    private static final int NUMBER_OF_BEACONS_EQUALS_ONE = 1;
    private static final int NUMBER_OF_OBSERVERS_EQUALS_ONE = 1;
    private static final double TRANSMISSION_THRESHOLD_RADIUS_EQUALS_ONE = 1.0;
    private static final int AWAKENESS_CYCLE_EQUALS_TWO = 2;
    private static final int AWAKENESS_DURATION_EQUALS_ONE = 1;
    private static final AwakenessStrategyFactory.Type FIXES_AWAKENESS_STRATEGY_TYPE =
            AwakenessStrategyFactory.Type.FIXED;

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
                .setBeaconMovementStrategyType(MOVE_UP)
                .setObserverMovementStrategyType(STATIONARY)
                .setAwakenessCycle(AWAKENESS_CYCLE_EQUALS_TWO)
                .setAwakenessDuration(AWAKENESS_DURATION_EQUALS_ONE)
                .setAwakenessStrategyType(FIXES_AWAKENESS_STRATEGY_TYPE)
                .build();

        // Find agents and calculate their expected location after a single round.
        Location beaconInitialLocation = findAgent(simulation.getBoard(), "Beacon");
        Location observerInitialLocation = findAgent(simulation.getBoard(), "Observer");
        Location beaconExpectedLocation = predictLocationAfterMoveUp(beaconInitialLocation);
        Location observerExpectedLocation = observerInitialLocation;

        // Run single-rounded simulation and find agents again.
        simulation.run();
        Location beaconActualLocation = findAgent(simulation.getBoard(), "Beacon");
        Location observerActualLocation = findAgent(simulation.getBoard(), "Observer");

        assertThat(beaconActualLocation).isEqualTo(beaconExpectedLocation);
        assertThat(observerActualLocation).isEqualTo(observerExpectedLocation);
    }

    private Location findAgent(Board board, String type) {
        Multimap<Location, IAgent> agentsOnLocations = board.agentsOnBoard();
        for(int row = 0; row < board.getRowNum(); row++) {
            for(int col = 0; col < board.getColNum(); col++) {
                Location loc = Location.create(row, col);
                for(IAgent agent : agentsOnLocations.get(loc)) {
                    if (agent.getId() == 0 && agent.getType().equals(type)) {
                        return loc;
                    }
                }
            }
        }
        return null;
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private Location predictLocationAfterMoveUp(Location location) {
        if(location.row() == 0) return location;
        return Location.create(location.row() - 1, location.col());
    }
}
