package com.google.research.bleth.simulator;

/** Represent a simulation. */
public abstract class Simulation {
    Board board;
    private int awakenessCycle;

    /** Return the simulation's board. */
    public Board getBoard() {
        return board;
    }

    /** Return the size of interval in which each observer has exactly one awakeness period. */
    public int getAwakenessCycle() {
        return awakenessCycle;
    }
}
