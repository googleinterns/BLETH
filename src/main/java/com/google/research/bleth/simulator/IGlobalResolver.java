package com.google.research.bleth.simulator;

/** Represent a trusted cloud global resolver. */
public interface IGlobalResolver extends IResolver {

    /** Update the estimated board which the resolver holds based on its current state and the information from the observers. */
    void estimate();

    /** Returns the estimated board that the resolver contains. */
    Board getBoard();
}
