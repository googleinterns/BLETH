package com.google.research.bleth.exceptions;

public class BoardStateAlreadyExistsException extends RuntimeException {
    public BoardStateAlreadyExistsException(String message) {
        super(message);
    }
}
