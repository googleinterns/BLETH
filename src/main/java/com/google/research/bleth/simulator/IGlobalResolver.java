package com.google.research.bleth.simulator;

import java.util.Map;

/** Represent a trusted cloud global resolver. */
public interface IGlobalResolver extends IResolver {

    /** Update the estimated board which the resolver holds based on its current state and the information from the observers. */
    void estimate();

    /** Returns the estimated board that the resolver contains. */
    EstimatedBoard getBoard();

    /** Returns the map between each beacon and its estimated location. */
    Map<Beacon, Location> getBeaconsToEstimatedLocations();
}
