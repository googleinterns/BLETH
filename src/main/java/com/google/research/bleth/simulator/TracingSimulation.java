package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

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
        void validateArguments() {
            // todo: validate simulation id is unique
            checkArgument(rowNum > 0 && colNum > 0,
                    "Board dimensions must be positive.");
            checkArgument(beaconsNum > 0 && observersNum > 0,
                    "Number of beacons and number of observers must be positive.");
            checkArgument(radius > 0,
                    "Transmission radius must be positive.");
            checkArgument(maxNumberOfRounds > 0,
                    "Maximum number of rounds must be positive.");
            checkNotNull(beaconMovementStrategy, "No beacon movement strategy has been set.");
            checkNotNull(observerMovementStrategy, "No observer movement strategy has been set.");
            checkNotNull(awakenessStrategyFactory, "No awakeness strategy factory has been set.");
        }

        @Override
        void initializeObservers() { }

        @Override
        void initializeBeacons() { }

        @Override
        public AbstractSimulation build() {
            validateArguments();
            initializeBeacons();
            initializeObservers();
            this.realBoard = new RealBoard(this.rowNum, this.colNum);
            this.resolver = new GlobalResolver(this.rowNum, this.colNum);
            return new TracingSimulation(this);
        }
    }

    public static class BuilderFromExisting extends AbstractSimulation.BuilderFromExisting {

        @Override
        void validateArguments() {
            // todo: validate simulation id is unique
            checkNotNull(realBoard);
            checkNotNull(resolver);
            checkArgument(resolver instanceof GlobalResolver,
                    "Tracing simulation resolver must be a global resolver.");
            checkArgument(realBoard.getRowNum() == resolver.getBoard().getRowNum() &&
                            realBoard.getColNum() == resolver.getBoard().getColNum(),
                    "Real and estimated board dimensions must agree.");
            checkArgument(!beacons.isEmpty() && !observers.isEmpty(),
                    "Number of beacons and number of observers must be positive.");
            checkArgument(radius > 0,
                    "Transmission radius must be positive.");
            checkArgument(currentRound >= 0,
                    "Current round index must be non-negative.");
            checkArgument(currentRound <= maxNumberOfRounds,
                    "Current round index must be less of equal to the index of last round " +
                            "(i.e. max number of rounds).");
            checkNotNull(beaconMovementStrategy, "No beacon movement strategy has been set.");
            checkNotNull(observerMovementStrategy, "No observer movement strategy has been set.");
            checkNotNull(awakenessStrategyFactory, "No awakeness strategy factory has been set.");
        }

        @Override
        public AbstractSimulation build() {
            validateArguments();
            this.rowNum = this.realBoard.getRowNum();
            this.colNum = this.realBoard.getColNum();
            this.beaconsNum = beacons.size();
            this.observersNum = observers.size();
            return new TracingSimulation(this);
        }
    }

    private TracingSimulation(Builder builder) {
        super(builder.id, builder.currentRound, builder.maxNumberOfRounds, builder.resolver, builder.radius,
                builder.realBoard, builder.beacons, builder.observers);
    }

    private TracingSimulation(BuilderFromExisting builder) {
        super(builder.id, builder.currentRound, builder.maxNumberOfRounds, builder.resolver, builder.radius,
                builder.realBoard, builder.beacons, builder.observers);
    }
}
