package com.google.research.bleth.exceptions;

/** An exception class to indicate a provided round number exceeds a simulation's maximum number of rounds. */
public class ExceedingRoundException extends RuntimeException {
    public ExceedingRoundException(String message) {
        super(message);
    }
}
