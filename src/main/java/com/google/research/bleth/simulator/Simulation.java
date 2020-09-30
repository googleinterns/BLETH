package com.google.research.bleth.simulator;

public abstract class Simulation {
    Board board;
    private int awakenessCycle;

    public Board getBoard() {
        return board;
    }

    public int getAwakenessCycle() {
        return awakenessCycle;
    }
}
