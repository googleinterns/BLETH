package com.google.research.bleth.servlets;

import com.google.research.bleth.services.DatabaseService;
import com.google.research.bleth.simulator.Board;
import com.google.research.bleth.simulator.DummyBeacon;
import com.google.research.bleth.simulator.Location;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/write-hard-coded-board-state")
public class WriteHardCodedBoardStatesServlet extends HttpServlet {

    private DatabaseService datastore = DatabaseService.getInstance();
    private Board realBoard;
    private Board estimatedBoard;
    private DummyBeacon firstDummyBeacon;
    private DummyBeacon secondDummyBeacon;
    private DummyBeacon thirdDummyBeacon;

    @Override
    public void init() {
        realBoard = new Board(5, 5);
        estimatedBoard = new Board(5, 5);
        firstDummyBeacon = new DummyBeacon(0, new Location((int) (Math.random() * 3),(int) (Math.random() * 3)));
        secondDummyBeacon = new DummyBeacon(1, new Location((int) (Math.random() * 3),(int) (Math.random() * 3)));
        thirdDummyBeacon = new DummyBeacon(2, new Location((int) (Math.random() * 3),(int) (Math.random() * 3)));
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String simulationId = "demo-tracing-sim-1";
        for (int round = 0; round < 10; round++) {
            realBoard = new Board(5, 5);
            estimatedBoard = new Board(5, 5);

            realBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), firstDummyBeacon);
            realBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), secondDummyBeacon);
            realBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), thirdDummyBeacon);

            estimatedBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), firstDummyBeacon);
            estimatedBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), secondDummyBeacon);
            estimatedBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), thirdDummyBeacon);

            datastore.writeBoardState(simulationId, round, realBoard.getState(), true);
            datastore.writeBoardState(simulationId, round, estimatedBoard.getState(), false);
        }

        response.sendRedirect("/index.html");
    }
}
