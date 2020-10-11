package com.google.research.bleth.simulator;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An abstract class representing a BLETH simulation.
 * Can be either a Tracing simulation or a Stalking simulation.
 */
public abstract class Simulation {

    private final String id;
    private int currentRound;
    private final int maxNumberOfRounds;
    private Board board;
    protected final ImmutableList<Beacon> beacons;
    protected final ImmutableList<Observer> observers;
    private IResolver resolver;
    private final double radius;
    private HashMap<String, Double> stats = new HashMap<>();

    // A protected constructor used by the concrete simulation classes' constructors.
    protected Simulation
    (String id, int currentRound, int maxNumberOfRounds, IResolver resolver, double radius, Board realBoard,
     List<Beacon> beacons, List<Observer> observers) {
        this.id = id;
        this.maxNumberOfRounds = maxNumberOfRounds;
        this.board = realBoard;
        this.resolver = resolver;
        this.radius = radius;
        this.beacons = ImmutableList.copyOf(beacons);
        this.observers = ImmutableList.copyOf(observers);
    }

    /**
     * Return the simulation real board.
     * @return Board object representing the simulation's real state.
     */
    Board getBoard() {
        return board;
    }

    /**
     * Run entire simulation logic, including writing data to db.
     */
    public void run() {
        if (currentRound == 0) {
            writeRoundState(); // round 0 is the initial simulation state
            currentRound++;
        }
        while (currentRound <= maxNumberOfRounds) {
            moveAgents();
            updateObserversAwaknessState();
            beaconsToObservers();
            observersToResolver();
            resolverEstimate();
            writeRoundState();
            updateSimulationStats();
            currentRound++;
        }
        writeSimulationStats();
    }

    /**
     * Move all agents according to their movement strategies and update the real board.
     */
    void moveAgents() { }

    /**
     * Update all observers awakeness states according to their awakeness strategies.
     */
    void updateObserversAwaknessState() { }

    /**
     * Pass transmissions from beacons to observers while taking into consideration world-physics parameters,
     * such as probability of transmission and distance between beacons and observers.
     */
    void beaconsToObservers() { }

    /**
     * Pass current-round information of transmission data from all observers to the simulation's resolver.
     */
    void observersToResolver() { }

    /**
     * Update resolver's estimated board.
     */
    void resolverEstimate() { }

    /**
     * Write current-round state of the simulation to db.
     */
    void writeRoundState() { }

    /**
     * Gather statistical data of the current round and update the aggregated simulation statistics based on all rounds.
     */
    abstract void updateSimulationStats();

    /**
     * Write final simulation statistical data to db.
     */
    void writeSimulationStats() { }

    /**
     * An abstract builder class designed to separate the construction of a simulation from its representation.
     * Designated for the construction of a new simulation, based on parameters given by an end-user.
     */
    public static abstract class SimulationBuilder {

        protected String id;
        protected int currentRound = 0;
        protected int maxNumberOfRounds;
        protected int rowNum;
        protected int colNum;
        protected Board realBoard;
        protected IResolver resolver;
        protected int beaconsNum;
        protected int observersNum;
        protected List<Beacon> beacons = new ArrayList<>();
        protected List<Observer> observers = new ArrayList<>();
        protected IMovementStrategy beaconMovementStrategy;
        protected IMovementStrategy observerMovementStrategy;
        protected AwakenessStrategyFactory awakenessStrategyFactory;
        protected double radius;

        /**
         * Set simulation id.
         * @param id is a unique simulation id.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set maximum number of rounds in simulation.
         * @param maxNumberOfRounds is the maximum number of rounds in simulation.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setMaxNumberOfRounds(int maxNumberOfRounds) {
            this.maxNumberOfRounds = maxNumberOfRounds;
            return this;
        }

        /**
         * Set number of rows in simulation.
         * @param rowNum is the number of rows in simulation.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setRowNum(int rowNum) {
            this.rowNum = rowNum;
            return this;
        }

        /**
         * Set number of columns in simulation.
         * @param colNum is the number of columns in simulation.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setColNum(int colNum) {
            this.colNum = colNum;
            return this;
        }

        /**
         * Set number of beacons in simulation.
         * @param beaconsNum is the number of beacons in simulation.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setBeaconsNum(int beaconsNum) {
            this.beaconsNum = beaconsNum;
            return this;
        }

        /**
         * Set number of observers in simulation.
         * @param observersNum is the number of observers in simulation.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setObserversNum(int observersNum) {
            this.observersNum = observersNum;
            return this;
        }

        /**
         * Set the beacons' movement strategy.
         * @param beaconMovementStrategy is the movement strategy for all beacons.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setBeaconMovementStrategy(IMovementStrategy beaconMovementStrategy) {
            this.beaconMovementStrategy = beaconMovementStrategy;
            return this;
        }

        /**
         * Set the observers' movement strategy.
         * @param observerMovementStrategy is the movement strategy for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setObserverMovementStrategy(IMovementStrategy observerMovementStrategy) {
            this.observerMovementStrategy = observerMovementStrategy;
            return this;
        }

        /**
         * Set the observers' awakeness strategy factory, used for generating awakeness strategies for all observers.
         * @param awakenessStrategyFactory is the awakeness strategy factory for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setAwakenessStrategyFactory(AwakenessStrategyFactory awakenessStrategyFactory) {
            this.awakenessStrategyFactory = awakenessStrategyFactory;
            return this;
        }

        /**
         * Set the threshold transmission radius.
         * @param radius is the threshold transmission radius
         * @return this, to provide chaining.
         */
        public SimulationBuilder setRadius(double radius) {
            this.radius = radius;
            return this;
        }

        /**
         * Validate all simulation builder arguments are legal.
         */
        abstract void validateArguments();

        /**
         * Create and initialize simulation observers using a factory, and store them in observers container.
         * Initializes the observers movement strategy according to the strategy passed to the builder.
         * Initializes the observers awakeness strategy according to the strategy factory passed to the builder.
         */
        abstract void initializeObservers();

        /**
         * Create and initialize simulation beacons using a factory, and store them in observers container.
         * Create simple beacons for a tracing simulation and swapping beacons for a stalking simulation.
         * Initializes the beacons movement strategy according to the strategy passed to the builder.
         */
        abstract void initializeBeacons();

        /**
         * Construct a simulation object.
         * @return a new Simulation object constructed with the builder parameters.
         */
        public abstract Simulation build();
    }

    /**
     * An abstract builder class designed to separate the construction of a simulation from its representation.
     * Designated for the construction of a simulation based on old simulation state read from the db.
     */
    public static abstract class SimulationBuilderFromExisting {

        protected String id;
        protected int currentRound = 0;
        protected int maxNumberOfRounds;
        protected int rowNum;
        protected int colNum;
        protected Board realBoard;
        protected IResolver resolver;
        protected int beaconsNum;
        protected int observersNum;
        protected List<Beacon> beacons = new ArrayList<>();
        protected List<Observer> observers = new ArrayList<>();
        protected IMovementStrategy beaconMovementStrategy;
        protected IMovementStrategy observerMovementStrategy;
        protected AwakenessStrategyFactory awakenessStrategyFactory;
        protected double radius;

        /**
         * Set simulation id.
         * @param id is a unique simulation id.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Set initial round index.
         * @param currentRound is the round index to start the new simulation run from.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setCurrentRound(int currentRound) {
            this.currentRound = currentRound;
            return this;
        }

        /**
         * Set maximum number of rounds in simulation as the index of the last simulation round.
         * @param maxNumberOfRounds is the maximum number of rounds in simulation (index of last round).
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setMaxNumberOfRounds(int maxNumberOfRounds) {
            this.maxNumberOfRounds = maxNumberOfRounds;
            return this;
        }

        /**
         * Set initial real board of simulation.
         * @param realBoard is the real board.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setRealBoard(Board realBoard) {
            this.realBoard = realBoard;
            return this;
        }

        /**
         * Set simulation resolver.
         * @param resolver is the resolver.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setResolver(IResolver resolver) {
            this.resolver = resolver;
            return this;
        }

        /**
         * Set list of beacons associated with the simulation.
         * @param beacons is the list of beacons.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setBeacons(List<Beacon> beacons) {
            this.beacons = beacons;
            return this;
        }

        /**
         * Set list of observers associated with the simulation.
         * @param observers is the list of observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setObservers(List<Observer> observers) {
            this.observers = observers;
            return this;
        }

        /**
         * Set the beacons' movement strategy.
         * @param beaconMovementStrategy is the movement strategy for all beacons.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setBeaconMovementStrategy(IMovementStrategy beaconMovementStrategy) {
            this.beaconMovementStrategy = beaconMovementStrategy;
            return this;
        }

        /**
         * Set the observers' movement strategy.
         * @param observerMovementStrategy is the movement strategy for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setObserverMovementStrategy(IMovementStrategy observerMovementStrategy) {
            this.observerMovementStrategy = observerMovementStrategy;
            return this;
        }

        /**
         * Set the observers' awakeness strategy factory, used for generating awakeness strategies for all observers.
         * @param awakenessStrategyFactory is the awakeness strategy factory for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setAwakenessStrategyFactory(AwakenessStrategyFactory awakenessStrategyFactory) {
            this.awakenessStrategyFactory = awakenessStrategyFactory;
            return this;
        }

        /**
         * Set the threshold transmission radius.
         * @param radius is the threshold transmission radius
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setRadius(double radius) {
            this.radius = radius;
            return this;
        }

        /**
         * Validate all simulation builder arguments are legal.
         */
        abstract void validateArguments();

        /**
         * Construct a simulation object.
         * @return a new Simulation object constructed with the builder parameters.
         */
        public abstract Simulation build();
    }
}
