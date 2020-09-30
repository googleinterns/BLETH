package com.google.research.bleth.simulator;

import java.util.Random;

public class TracingSimulation extends Simulation {

    protected TracingSimulation
            (String id, int maxNumberOfRounds, int rowNum, int colNum,
             int beaconsNum, int observersNum, int awakenessCycle, double radius,
             MovementStrategy beaconMovementStrategy, MovementStrategy observerMovementStrategy) {

        super(id, maxNumberOfRounds, rowNum, colNum, beaconsNum, observersNum, new GlobalResolver(),
                awakenessCycle, radius, beaconMovementStrategy, observerMovementStrategy);
    }

    @Override
    void initializeBeacons() {
        Random rand = new Random();
        for (int i = 0; i < beaconsNum; i++) {
            // Generate random initial location.
            Location initialLocation = new Location(rand.nextInt(this.rowNum), rand.nextInt(this.colNum));
            // Assign movement strategy.
            MovementStrategy movementStrategy = this.beaconMovementStrategy;
            // Add beacon.
            beacons.add(new BeaconFactory().createBeacon(initialLocation, movementStrategy, this));
        }
    }

    @Override
    void updateSimulationStats() {

    }
}
