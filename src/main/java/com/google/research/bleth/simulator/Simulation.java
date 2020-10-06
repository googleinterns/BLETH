package com.google.research.bleth.simulator;

import com.google.common.collect.ImmutableList;

import java.util.ArrayList;

/**
 * An abstract class representing a BLETH simulation.
 * Can be either a Tracing simulation or a Stalking simulation.
 */
public abstract class Simulation {

    private final String id;
    protected int currentRound = 0;
    private final int maxNumberOfRounds;
    protected final int rowNum;
    protected final int colNum;
    private Board board;
    protected final int beaconsNum;
    protected final int observersNum;
    protected ArrayList<Beacon> beacons;
    protected ArrayList<Observer> observers;
    protected final MovementStrategy beaconMovementStrategy;
    protected final MovementStrategy observerMovementStrategy;
    protected final AwakenessStrategy awakenessStrategy;
    private IResolver resolver;
    private final int awakenessCycle;       // size of interval in which each observer has exactly one awakeness period
    private final int awakenessDuration;    // size of interval in which each observer is awake
    private final double radius;            // threshold transmission radius
    protected boolean initializedFromExisting;
    // todo: add hashmap object to store stats
    // todo: add db service instance

    // A protected constructor used by the concrete simulation classes' constructors.
    protected Simulation
    (String id, int maxNumberOfRounds, int rowNum,
     int colNum, int beaconsNum, int observersNum,
     IResolver resolver, int awakenessCycle, int awakenessDuration,
     double radius, MovementStrategy beaconMovementStrategy, MovementStrategy observerMovementStrategy,
     AwakenessStrategy awakenessStrategy, Board realBoard, boolean initializedFromExisting,
     ArrayList<Beacon> beacons, ArrayList<Observer> observers) {
        this.id = id;
        this.maxNumberOfRounds = maxNumberOfRounds;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.board = realBoard;
        this.beaconsNum = beaconsNum;
        this.observersNum = observersNum;
        this.resolver = resolver;
        this.awakenessCycle = awakenessCycle;
        this.awakenessDuration = awakenessDuration;
        this.radius = radius;
        this.beaconMovementStrategy = beaconMovementStrategy;
        this.observerMovementStrategy = observerMovementStrategy;
        this.awakenessStrategy = awakenessStrategy;
        this.initializedFromExisting = initializedFromExisting;
        this.beacons = beacons;
        this.observers = observers;
    }

    /**
     * Return the simulation real board.
     * @return Board object representing the simulation's real state.
     */
    Board getBoard() {
        return board;
    }

    /**
     * Return an immutable copy of the simulation's beacons list.
     * @return ImmutableList containing the simulation's beacons
     */
    ImmutableList<Beacon> getBeacons() {
        return ImmutableList.copyOf(beacons);
    }

    /**
     * Run entire simulation logic, including writing data to db.
     */
    public void run() {
        if (!initializedFromExisting) {
            initializeBeacons();
            initializeObservers();
        }
        writeRoundState(); // round 0 is the initial simulation state
        for (int round = 1; round <= maxNumberOfRounds; round++, currentRound++) {
            moveAgents();
            updateObserversAwaknessState();
            beaconsToObservers();
            observersToResolver();
            resolverEstimate();
            writeRoundState();
            updateSimulationStats();
        }
        writeSimulationStats();
    }

    /**
     * Create and initialize simulation observers using a factory, and store them in observers container.
     */
    void initializeObservers() { }

    /**
     * Create and initialize simulation observers using a factory, and store them in observers container.
     * Create simple beacons for a tracing simulation and swapping beacons for a stalking simulation.
     */
    abstract void initializeBeacons();

    /**
     * Move all agents according to their movement strategies and update the real board.
     */
    void moveAgents() {
        beacons.stream().forEach(beacon -> board.moveAgent(beacon.getLocation(), beacon.moveTo(), beacon));
        // todo: move observers
    }

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
     * Write current-round real and estimated board states to db.
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
     * Designated for the construction of a new simulation, based on simulation given by an end-user.
     */
    public static abstract class SimulationBuilder {

        protected String id;
        protected int maxNumberOfRounds;
        protected int rowNum;
        protected int colNum;
        protected Board realBoard;
        protected IResolver resolver;
        protected int beaconsNum;
        protected int observersNum;
        protected ArrayList<Beacon> beacons = new ArrayList<>();
        protected ArrayList<Observer> observers = new ArrayList<>();
        protected MovementStrategy beaconMovementStrategy;
        protected MovementStrategy observerMovementStrategy;
        protected AwakenessStrategy awakenessStrategy;
        protected int awakenessCycle;
        protected int awakenessDuration;
        protected double radius;
        protected boolean initializedFromExisting = false;

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
        public SimulationBuilder setBeaconMovementStrategy(MovementStrategy beaconMovementStrategy) {
            this.beaconMovementStrategy = beaconMovementStrategy;
            return this;
        }

        /**
         * Set the observers' movement strategy.
         * @param observerMovementStrategy is the movement strategy for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setObserverMovementStrategy(MovementStrategy observerMovementStrategy) {
            this.observerMovementStrategy = observerMovementStrategy;
            return this;
        }

        /**
         * Set the observers' awakeness strategy.
         * @param awakenessStrategy is the awakeness strategy for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setAwakenessStrategy(AwakenessStrategy awakenessStrategy) {
            this.awakenessStrategy = awakenessStrategy;
            return this;
        }

        /**
         * Set the observers' awakeness cycle - size of interval in which each observer has exactly one awakeness period.
         * @param awakenessCycle is the awakeness cycle for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setAwakenessCycle(int awakenessCycle) {
            this.awakenessCycle = awakenessCycle;
            return this;
        }

        /**
         * Set the observers' awakeness duration - size of interval in which each observer is awake.
         * @param awakenessDuration is the awakeness duration for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilder setAwakenessDuration(int awakenessDuration) {
            this.awakenessDuration = awakenessDuration;
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
         * Construct a simulation object.
         * @return a new Simulation object constructed with the builder parameters.
         */
        public abstract Simulation build();

        /**
         * Validate all simulation builder arguments are legal.
         */
        public abstract void validateArguments();
    }

    /**
     * An abstract builder class designed to separate the construction of a simulation from its representation.
     * Designated for the construction of a simulation based on old simulation state read from the db.
     */
    public static abstract class SimulationBuilderFromExisting {

        protected String id;
        protected int maxNumberOfRounds;
        protected int rowNum;
        protected int colNum;
        protected Board realBoard;
        protected IResolver resolver;
        protected int beaconsNum;
        protected int observersNum;
        protected ArrayList<Beacon> beacons = new ArrayList<>();
        protected ArrayList<Observer> observers = new ArrayList<>();
        protected MovementStrategy beaconMovementStrategy;
        protected MovementStrategy observerMovementStrategy;
        protected AwakenessStrategy awakenessStrategy;
        protected int awakenessCycle;
        protected int awakenessDuration;
        protected double radius;
        protected boolean initializedFromExisting = true;

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
         * Set maximum number of rounds in simulation.
         * @param maxNumberOfRounds is the maximum number of rounds in simulation.
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
        public SimulationBuilderFromExisting setBeacons(ArrayList<Beacon> beacons) {
            this.beacons = beacons;
            return this;
        }

        /**
         * Set list of observers associated with the simulation.
         * @param observers is the list of observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setObservers(ArrayList<Observer> observers) {
            this.observers = observers;
            return this;
        }

        /**
         * Set the beacons' movement strategy.
         * @param beaconMovementStrategy is the movement strategy for all beacons.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setBeaconMovementStrategy(MovementStrategy beaconMovementStrategy) {
            this.beaconMovementStrategy = beaconMovementStrategy;
            return this;
        }

        /**
         * Set the observers' movement strategy.
         * @param observerMovementStrategy is the movement strategy for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setObserverMovementStrategy(MovementStrategy observerMovementStrategy) {
            this.observerMovementStrategy = observerMovementStrategy;
            return this;
        }

        /**
         * Set the observers' awakeness strategy.
         * @param awakenessStrategy is the awakeness strategy for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setAwakenessStrategy(AwakenessStrategy awakenessStrategy) {
            this.awakenessStrategy = awakenessStrategy;
            return this;
        }

        /**
         * Set the observers' awakeness cycle - size of interval in which each observer has exactly one awakeness period.
         * @param awakenessCycle is the awakeness cycle for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setAwakenessCycle(int awakenessCycle) {
            this.awakenessCycle = awakenessCycle;
            return this;
        }

        /**
         * Set the observers' awakeness duration - size of interval in which each observer is awake.
         * @param awakenessDuration is the awakeness duration for all observers.
         * @return this, to provide chaining.
         */
        public SimulationBuilderFromExisting setAwakenessDuration(int awakenessDuration) {
            this.awakenessDuration = awakenessDuration;
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
         * Construct a simulation object.
         * @return a new Simulation object constructed with the builder parameters.
         */
        public abstract Simulation build();

        /**
         * Validate all simulation builder arguments are legal.
         */
        public abstract void validateArguments();
    }
}
