package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Random;

/**
 * A simulation in which the global resolver estimates beacons' real locations based on information received from observers.
 * Beacons transmit their unique static IDs each round.
 * Agents move according to a movement strategy.
 * Observers change their awakeness states according to an awakeness strategy.
 * Location estimation occurs each round and are based on both previous estimations and newly received information.
 */
public class TracingSimulation extends AbstractSimulation {

    @Override
    void updateSimulationStats() { }

    public static class Builder extends AbstractSimulation.Builder {

        @Override
        void writeSimulationMetadata() {
            // todo: write metadata and assign a unique simulation id
        }

        @Override
        void validateArguments() {
            checkArgument(rowNum > 0 && colNum > 0,
                    "Board dimensions must be positive.");
            checkArgument(beaconsNum > 0 && observersNum > 0,
                    "Number of beacons and number of observers must be positive.");
            checkArgument(awakenessCycle > 0 && awakenessDuration > 0,
                    "Both awakeness cycle and duration must be positive.");
            checkArgument(awakenessCycle >= awakenessDuration,
                    "Awakeness cycle must be greater or equal to duration.");
            checkArgument(transmissionThresholdRadius > 0,
                    "Transmission threshold radius must be positive.");
            checkArgument(maxNumberOfRounds > 0,
                    "Maximum number of rounds must be positive.");
            checkNotNull(beaconMovementStrategy, "No beacon movement strategy has been set.");
            checkNotNull(observerMovementStrategy, "No observer movement strategy has been set.");
            checkNotNull(awakenessStrategyType, "No awakeness strategy type has been set.");
        }

        @Override
        void initializeObservers() {
            Random rand = new Random();
            ObserverFactory observerFactory = new ObserverFactory();
            AwakenessStrategyFactory awakenessStrategyFactory = new AwakenessStrategyFactory(awakenessStrategyType);
            for (int i = 0; i < observersNum; i++) {
                Location initialLocation = new Location(rand.nextInt(rowNum), rand.nextInt(colNum));
                IAwakenessStrategy awakenessStrategy = awakenessStrategyFactory.createStrategy(awakenessCycle, awakenessDuration);
                Observer observer = observerFactory.createObserver(initialLocation, observerMovementStrategy, resolver,
                realBoard, awakenessStrategy);
                observers.add(observer);
            }
        }

        @Override
        void initializeBeacons() {
            Random rand = new Random();
            BeaconFactory beaconFactory = new BeaconFactory();
            for (int i = 0; i < beaconsNum; i++) {
                Location initialLocation = new Location(rand.nextInt(rowNum), rand.nextInt(colNum));
                Beacon beacon = beaconFactory.createBeacon(initialLocation, beaconMovementStrategy, realBoard);
                beacons.add(beacon);
            }
        }

        @Override
        public AbstractSimulation build() {
            validateArguments();
            this.realBoard = new RealBoard(this.rowNum, this.colNum);
            this.resolver = new GlobalResolver(this.rowNum, this.colNum);
            initializeBeacons();
            initializeObservers();
            writeSimulationMetadata();
            return new TracingSimulation(this);
        }
    }

    private TracingSimulation(Builder builder) {
        super(builder);
    }
}
