package com.google.research.bleth.simulator;

import java.util.Random;

/**
 * A simulation in which the global resolver estimates beacons real locations based on information received from observers.
 * Beacons are moving on the board and transmitting their unique static IDs each round.
 * Location estimation occurs each round and based on both previous estimations and newly received information.
 *
 * Running a tracing simulation includes:
 * 1. performing all simulation logic
 * 2. writing to db real and estimated board states at the end of each round
 * 3. writing to db statistical data at the end of the simulation
 */
public class TracingSimulation extends Simulation {

    private TracingSimulation(TracingSimulationBuilder builder) {

        super(builder.id, builder.maxNumberOfRounds, builder.rowNum, builder.colNum,
                builder.beaconsNum, builder.observersNum, new GlobalResolver(), builder.awakenessCycle,
                builder.awakenessDuration, builder.radius, builder.beaconMovementStrategy, builder.observerMovementStrategy);
    }

    @Override
    void initializeBeacons() {
        BeaconFactory factory = new BeaconFactory();
        Random rand = new Random();
        for (int i = 0; i < beaconsNum; i++) {
            // Generate random initial location.
            Location initialLocation = new Location(rand.nextInt(this.rowNum), rand.nextInt(this.colNum));
            // Add beacon.
            beacons.add(factory.createBeacon(initialLocation, this.beaconMovementStrategy, this));
        }
    }

    @Override
    void updateSimulationStats() {

    }

    public static class TracingSimulationBuilder extends SimulationBuilder {

        @Override
        public Simulation build() throws Exception {
            TracingSimulation simulation = new TracingSimulation(this);
            validateSimulation(simulation);
            return simulation;
        }

        // todo: implement
        // todo: specify exception type
        @Override
        public boolean validateSimulation(Simulation simulation) throws Exception {
            // validation logic
            return true;
        }
    }
}
