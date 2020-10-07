package com.google.research.bleth.simulator;

import java.util.List;

public class GlobalResolver implements IGlobalResolver {
    private final int rowNum;
    private final int colNum;

    public GlobalResolver(int rowNum, int colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    @Override
    public void receiveInformation(Location observerLocation, List<Transmission> transmissions) {

    }

    @Override
    public Board getBoard() {
        return null;
    }
}
