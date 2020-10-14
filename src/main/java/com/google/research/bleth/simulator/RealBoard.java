package com.google.research.bleth.simulator;

/** The real board that the simulation holds. Represents the real world and contains the beacons' real locations. */
public class RealBoard extends Board implements IAgentOwner {

    /**
     * Create an empty board for storing the real agents' locations in the simulation.
     * @param rows is number of rows.
     * @param cols is number of columns.
     */
    public RealBoard(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public String getType() {
        return "RealBoard";
    }

    @Override
    public void updateAgentLocation(Location oldLocation, Location newLocation, IAgent agent) {
        if (oldLocation == null) {
            placeAgent(newLocation, agent);
        } else {
            moveAgent(oldLocation, newLocation, agent);
        }
    }
}
