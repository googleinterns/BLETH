package com.google.research.bleth.simulator;

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

    public int getRowDelta() {
        return rowDelta;
    }

    public int getColDelta() {
        return colDelta;
    }
}
