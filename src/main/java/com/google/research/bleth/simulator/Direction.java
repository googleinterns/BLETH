package com.google.research.bleth.simulator;

/** Possible directions an agent can move to. */
public enum Direction {
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    UP(-1, 0);

    private final int rowDelta;
    private final int colDelta;

    Direction(int rowDelta, int colDelta) {
        this.rowDelta = rowDelta;
        this.colDelta = colDelta;
    }

    /** Return the delta of the row index required to move in the specific direction. */
    public int getRowDelta() {
        return rowDelta;
    }

    /** Return the delta of the col index required to move in the specific direction. */
    public int getColDelta() {
        return colDelta;
    }
}
