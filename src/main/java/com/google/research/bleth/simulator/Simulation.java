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

    Board getBoard() {
        return board;
    }

    ImmutableList<Beacon> getBeacons() {
        return ImmutableList.copyOf(beacons);
    }

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

    void initializeObservers() { }

    abstract void initializeBeacons();

    void moveAgents() {
        beacons.stream().forEach(beacon -> board.placeAgent(beacon.moveTo(), beacon));
        // todo: observers.stream().forEach(observer -> board.placeAgent(observer.moveTo(), observer));
    }

    void updateObserversAwaknessState() { }

    void beaconsToObservers() { }

    void observersToResolver() { }

    void resolverEstimate() { }

    void writeRoundState() { }

    abstract void updateSimulationStats();

    void writeSimulationStats() { }

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

        public SimulationBuilder setId(String id) {
            this.id = id;
            return this;
        }

        public SimulationBuilder setMaxNumberOfRounds(int maxNumberOfRounds) {
            this.maxNumberOfRounds = maxNumberOfRounds;
            return this;
        }

        public SimulationBuilder setRowNum(int rowNum) {
            this.rowNum = rowNum;
            return this;
        }

        public SimulationBuilder setColNum(int colNum) {
            this.colNum = colNum;
            return this;
        }

        public SimulationBuilder setBeaconsNum(int beaconsNum) {
            this.beaconsNum = beaconsNum;
            return this;
        }

        public SimulationBuilder setObserversNum(int observersNum) {
            this.observersNum = observersNum;
            return this;
        }

        public SimulationBuilder setBeaconMovementStrategy(MovementStrategy beaconMovementStrategy) {
            this.beaconMovementStrategy = beaconMovementStrategy;
            return this;
        }

        public SimulationBuilder setObserverMovementStrategy(MovementStrategy observerMovementStrategy) {
            this.observerMovementStrategy = observerMovementStrategy;
            return this;
        }

        public SimulationBuilder setAwakenessCycle(int awakenessCycle) {
            this.awakenessCycle = awakenessCycle;
            return this;
        }

        public SimulationBuilder setAwakenessDuration(int awakenessDuration) {
            this.awakenessDuration = awakenessDuration;
            return this;
        }

        public SimulationBuilder setRadius(double radius) {
            this.radius = radius;
            return this;
        }

        public abstract Simulation build() throws Exception;

        public abstract boolean validateSimulation(Simulation simulation) throws Exception;
    }
}
