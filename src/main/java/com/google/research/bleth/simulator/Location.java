package com.google.research.bleth.simulator;

import com.google.auto.value.AutoValue;

/** A location on board. */
@AutoValue
public abstract class Location {
    public static Location create(int row, int col) {
        return new AutoValue_Location(row, col);
    }

    public abstract int row();
    public abstract int col();

    /**
     * Calculate and return the new location based on the current location and the given direction.
     * @param direction is the direction to move to it, one of the four directions: up, down, right and left.
     * @return new Location, differ from the original location in one step, according the given direction.
     */
    public Location moveInDirection(Direction direction) {
        return Location.create(row() + direction.getRowDelta(), col() + direction.getColDelta());
    }
}