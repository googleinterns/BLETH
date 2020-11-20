package com.google.research.bleth.simulator;

/** The estimated board that the resolver holds. Contains the beacons' estimated locations. */
public class EstimatedBoard extends AbstractBoard {

    /**
     * Create an empty board for storing beacons' estimated locations from the resolver's point of view.
     * @param rows is number of rows.
     * @param cols is number of columns.
     */
    public EstimatedBoard(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public String getType() {
        return "EstimatedBoard";
    }
}
