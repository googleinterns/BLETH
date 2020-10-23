package com.google.research.bleth.exceptions;

/** An exception class to indicate a simulation's board state in a certain round have been already recorded on ab. . */
public class BoardStateAlreadyExistsException extends RuntimeException {
    public BoardStateAlreadyExistsException(String message) {
        super(message);
    }
}
