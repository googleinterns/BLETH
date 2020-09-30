package com.google.research.bleth.simulator;

import java.util.ArrayList;

public abstract class Simulation {

    private final String id;            // unique simulation id
    private int currentRound = 0;
    private final int maxNumberOfRounds;
    protected final int rowNum;
    protected final int colNum;
    private Board board;                // real board
    protected final int beaconsNum;
    protected final int observersNum;
    protected ArrayList<Beacon> beacons = new ArrayList<Beacon>();
//    private ArrayList<Observer> observers = new ArrayList<Observer>();
    private IResolver resolver;
    private final int awakenessCycle;   // size of interval in which each observer has exactly one awakeness period
    private final double radius;        // threshold transmission radius
    // todo: add some hashmap object to store stats - here or at sub classes
    // todo: add db service

    protected Simulation
            (String id, int maxNumberOfRounds, int rowNum, int colNum,
             int beaconsNum, int observersNum, IResolver resolver, int awakenessCycle, double radius) {
        this.id = id;
        this.maxNumberOfRounds = maxNumberOfRounds;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.board = new Board(rowNum, colNum);
        this.beaconsNum = beaconsNum;
        this.observersNum = observersNum;
        this.resolver = resolver;
        this.awakenessCycle = awakenessCycle;
        this.radius = radius;
    }

    Board getBoard() {
        return board;
    }

    public void run() {

        initializeBeacons();
        initializeObservers();
        writeRoundState();

        for (int round = 1; round <= maxNumberOfRounds; round++) {
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
        // observers.stream().forEach(observer -> board.placeAgent(observer.moveTo(), observer));
    }

    void updateObserversAwaknessState() { }

    void beaconsToObservers() { }

    void observersToResolver() { }

    void resolverEstimate() { }

    void writeRoundState() { }

    abstract void updateSimulationStats();

    void writeSimulationStats() { }
}
