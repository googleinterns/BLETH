package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A simulation in which the global resolver estimates beacons real locations based on information received from observers.
 * Beacons transmit their unique static IDs each round.
 * Agents move according to a movement strategy.
 * Observers change their awakeness states according to an awakeness strategy.
 * Location estimation occurs each round and are based on both previous estimations and newly received information.
 */
public class TracingSimulation extends Simulation {

    private TracingSimulation(TracingSimulationBuilder builder) {

        super(builder.id, builder.maxNumberOfRounds, builder.resolver, builder.radius, builder.realBoard,
                builder.beacons, builder.observers);
    }

    private TracingSimulation(TracingSimulationBuilderFromExisting builder) {

        super(builder.id, builder.maxNumberOfRounds, builder.resolver, builder.radius, builder.realBoard,
                builder.beacons, builder.observers);
    }

    @Override
    void updateSimulationStats() { }

    public static class TracingSimulationBuilder extends SimulationBuilder {

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
            checkNotNull(beaconMovementStrategy, "No beacon movement strategy has been set.");
            checkNotNull(observerMovementStrategy, "No observer movement strategy has been set.");
            checkNotNull(awakenessStrategyFactory, "No awakeness strategy factory has been set.");
        }

        @Override
        void initializeObservers() { }

        @Override
        void initializeBeacons() { }

        @Override
        public Simulation build() {
            validateArguments();
            initializeBeacons();
            initializeObservers();
            this.realBoard = new Board(this.rowNum, this.colNum);
            this.resolver = new GlobalResolver(this.rowNum, this.colNum);
            return new TracingSimulation(this);
        }
    }

    public static class TracingSimulationBuilderFromExisting extends SimulationBuilderFromExisting {

        @Override
        public void validateArguments() {
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
            checkArgument(maxNumberOfRounds > 0,
                    "Maximum number of rounds must be positive.");
            checkNotNull(beaconMovementStrategy, "No beacon movement strategy has been set.");
            checkNotNull(observerMovementStrategy, "No observer movement strategy has been set.");
            checkNotNull(awakenessStrategyFactory, "No awakeness strategy factory has been set.");
        }

        @Override
        public Simulation build() {
            validateArguments();
            this.rowNum = this.realBoard.getRowNum();
            this.colNum = this.realBoard.getColNum();
            this.beaconsNum = beacons.size();
            this.observersNum = observers.size();
            return new TracingSimulation(this);
        }
    }
}
