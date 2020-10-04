package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Random;

/**
 * A simulation in which the global resolver estimates beacons real locations based on information received from observers.
 * Beacons transmit their unique static IDs each round.
 * Agents move according to a movement strategy.
 * Observers change their awakeness states according to an awakeness strategy.
 * Location estimation occurs each round and are based on both previous estimations and newly received information.
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

    /**
     * An abstract builder class designed to separate the construction of a tracing simulation from its representation.
     */
    public static class TracingSimulationBuilder extends SimulationBuilder {

        @Override
        public Simulation build() {
            validateArguments();
            return new TracingSimulation(this);
        }

        @Override
        public void validateArguments() {
            // todo: validate simulation id is unique
            checkArgument(rowNum > 0 && colNum > 0,
                    "Board dimensions must be positive.");
            checkArgument(beaconsNum > 0 && observersNum > 0,
                    "Number of beacons and number of observers must be positive.");
            checkArgument(radius > 0,
                    "Transmission radius must be positive.");
            checkArgument(maxNumberOfRounds > 0,
                    "Maximum number of rounds must be positive.");
            checkArgument(awakenessDuration > 0 && awakenessCycle > 0,
                    "Awakeness cycle and duration must be positive.");
            checkArgument(awakenessCycle > awakenessDuration,
                    "Awakeness cycle must be larger than awakeness duration.");
            checkNotNull(beaconMovementStrategy, "No beacon movement strategy has been set.");
            checkNotNull(observerMovementStrategy, "No observer movement strategy has been set.");
            // todo: validate observer awakeness strategy has been set
        }
    }
}
