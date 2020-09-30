package com.google.research.bleth.simulator;

import java.util.List;

public interface IResolver {

    /**
     * Add the transmissions from an observer to the resolver database, which consists of
     * all the transmission its observer observed in the current round.
     * @param observerLocation is the current location of the observer that observed and passed the transmissions.
     * @param transmissions is all the beacons' transmissions the observer observed in the current round.
     */
    void receiveInformation(Location observerLocation, List<Transmission> transmissions);

    /**
     * Returns the estimated board that the resolver contains.
     */
    Board getBoard();
}