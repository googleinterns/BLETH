package com.google.research.bleth.simulator;

public enum Direction {
    Down(1, 0),
    Left(0, -1),
    Right(0, 1),
    Up(-1, 0);

    public final int row;
    public final int col;

    Direction(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
