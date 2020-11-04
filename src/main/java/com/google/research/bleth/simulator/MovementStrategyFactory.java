package com.google.research.bleth.simulator;

/**
 * A class for generating movement strategies based on strategy type provided.
 * The factory gets as an argument the specific type of strategy to generate.
 */
public class MovementStrategyFactory {

    public enum Type {
        STATIONARY(false), RANDOM(false), UP(true);

        private final boolean isTestOnly;

        Type(boolean isTestOnly) { this.isTestOnly = isTestOnly; }

        public boolean isTestOnly() { return isTestOnly; }
    }

    private Type type;

    /**
     * Create new MovementStrategyFactory.
     * @param type is the type of the movement strategies generated using the created factory.
     */
    public MovementStrategyFactory(Type type) {
        this.type = type;
    }

    /**
     * Create a new strategy according to the factory's type attribute.
     * @return a movement strategy (if no proper type was set, return random by default).
     */
    public IMovementStrategy createStrategy() {
        switch (this.type) {
            case STATIONARY: return createStationaryStrategy();
            case UP: return createUpStrategy();
            default: return createRandomStrategy();
        }
    }

    private IMovementStrategy createStationaryStrategy() {
        return new StationaryMovementStrategy();
    }

    private IMovementStrategy createUpStrategy() {
        return new UpMovementStrategy();
    }

    private IMovementStrategy createRandomStrategy() {
        return new RandomMovementStrategy();
    }
}
