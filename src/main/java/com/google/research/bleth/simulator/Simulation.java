package com.google.research.bleth.simulator;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;

/**
 * An abstract class representing a BLETH simulation.
 * Can be either a Tracing simulation or a Stalking simulation.
 */
public abstract class Simulation {

    private final String id;                // unique simulation id
    protected int currentRound = 0;
    private final int maxNumberOfRounds;
    protected final int rowNum;
    protected final int colNum;
    private Board board;                    // real board
    protected final int beaconsNum;
    protected final int observersNum;
    protected ArrayList<Beacon> beacons = new ArrayList<>();
    //todo: protected ArrayList<Observer> observers = new ArrayList<>();
    protected final MovementStrategy beaconMovementStrategy;
    protected final MovementStrategy observerMovementStrategy;
    private IResolver resolver;
    private final int awakenessCycle;       // size of interval in which each observer has exactly one awakeness period
    private final int awakenessDuration;    // size of interval in which each observer is awake
    private final double radius;            // threshold transmission radius

    // todo: add observers awakeness strategy
    // todo: add hashmap object to store stats
    // todo: add db service instance

    // A protected constructor used by the concrete simulation classes' constructors.
    protected Simulation
            (String id, int maxNumberOfRounds, int rowNum, int colNum,
             int beaconsNum, int observersNum, IResolver resolver, int awakenessCycle, int awakenessDuration, double radius,
             MovementStrategy beaconMovementStrategy, MovementStrategy observerMovementStrategy) {
        this.id = id;
        this.maxNumberOfRounds = maxNumberOfRounds;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.board = new Board(rowNum, colNum);
        this.beaconsNum = beaconsNum;
        this.observersNum = observersNum;
        this.resolver = resolver;
        this.awakenessCycle = awakenessCycle;
        this.awakenessDuration = awakenessDuration;
        this.radius = radius;
        this.beaconMovementStrategy = beaconMovementStrategy;
        this.observerMovementStrategy = observerMovementStrategy;
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

        initializeBeacons();
        initializeObservers();
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
        beacons.stream().forEach(beacon -> board.placeAgent(beacon.moveTo(), beacon));
        // todo: observers.stream().forEach(observer -> board.placeAgent(observer.moveTo(), observer));
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
     * An abstract builder class designed to separate the construction of a concrete simulation from its representation.
     */
    public static abstract class SimulationBuilder {

        protected String id;
        protected int maxNumberOfRounds;
        protected int rowNum;
        protected int colNum;
        protected int beaconsNum;
        protected int observersNum;
        protected MovementStrategy beaconMovementStrategy;
        protected MovementStrategy observerMovementStrategy;
        protected int awakenessCycle;
        protected int awakenessDuration;
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
         * @throws Exception
         */
        public abstract Simulation build() throws Exception;

        /**
         * Validate all simulation attributes.
         * @param simulation a simulation to check if valid.
         * @return true if valid.
         * @throws Exception
         */
        public abstract boolean validateSimulation(Simulation simulation) throws Exception;
    }
}
