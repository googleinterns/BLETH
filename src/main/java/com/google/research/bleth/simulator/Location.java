package com.google.research.bleth.simulator;

/** A location on board. */
public class Location {
    public final int row;
    public final int col;

    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Location location = (Location) o;
        return row == location.row && col == location.col;
    }
}
