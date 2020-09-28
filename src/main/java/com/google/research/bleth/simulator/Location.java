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

    /**
     * Calculate and return the new location based on the current location and the given direction.
     * @param direction is the direction to move to it, one of the four directions: up, down, right and left.
     * @return new Location, differ from the original location in one step, according the given direction.
     */
    public Location moveInDirection(Direction direction) {
        return new Location(row + direction.row, col + direction.col);
    }
}
