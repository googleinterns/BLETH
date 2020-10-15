package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class TracingSimulationIT {
    private static final IMovementStrategy MOVE_UP = new UpMovementStrategy();
    private static final IMovementStrategy STATIONARY = new StationaryMovementStrategy();

    private static final String SIMULATION_ID = "test-sim-id-1";
    private static final int BOARD_DIMENSION_EQUALS_TWO = 2;
    private static final int MAX_ROUNDS_EQUALS_ONE = 1;
    private static final int CURRENT_ROUND_EQUALS_ZERO = 0;
    private static final double RADIUS_EQUALS_ONE = 1.0;

    @Mock
    private GlobalResolver resolver;

    @Test
    public void runSimulationSingleRoundVerifyAgentsLocations() {
        // Create board, resolver and agents.
        RealBoard realBoard = new RealBoard(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        EstimatedBoard estimatedBoard = new EstimatedBoard(BOARD_DIMENSION_EQUALS_TWO, BOARD_DIMENSION_EQUALS_TWO);
        Mockito.when(resolver.getBoard()).thenReturn(estimatedBoard);
        ArrayList<Beacon> beacons = new ArrayList<>();
        ArrayList<Observer> observers = new ArrayList<>();

        BeaconFactory beaconFactory = new BeaconFactory();
        ObserverFactory observerFactory = new ObserverFactory();
        AwakenessStrategyFactory awakenessStrategyFactory = new AwakenessStrategyFactory(AwakenessStrategyFactory.Type.FIXED);
        Beacon beacon = beaconFactory.createBeacon(new Location(1, 0), MOVE_UP, realBoard);
        Observer observer = observerFactory.createObserver(new Location(0, 0), STATIONARY, resolver,
                realBoard, awakenessStrategyFactory.createStrategy(2, 1));

        beacons.add(beacon);
        observers.add(observer);

        // Create simulation.
        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setId(SIMULATION_ID)
                .setCurrentRound(CURRENT_ROUND_EQUALS_ZERO)
                .setMaxNumberOfRounds(MAX_ROUNDS_EQUALS_ONE)
                .setRadius(RADIUS_EQUALS_ONE)
                .setRealBoard(realBoard)
                .setResolver(resolver)
                .setBeacons(beacons)
                .setObservers(observers)
                .buildRestored();

        simulation.run();

        assertThat(beacon.getLocation()).isEqualTo(new Location(0, 0));
        assertThat(observer.getLocation()).isEqualTo(new Location(0, 0));
    }
}
