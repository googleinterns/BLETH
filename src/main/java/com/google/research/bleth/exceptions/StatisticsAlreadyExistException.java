package com.google.research.bleth.exceptions;

public class StatisticsAlreadyExistException extends RuntimeException {
    public StatisticsAlreadyExistException(String id) {
        super(id + " statistics already exist in db.");
    }

    public StatisticsAlreadyExistException() {
        super("Statistics of this simulation already exist in db.");
    }
}
