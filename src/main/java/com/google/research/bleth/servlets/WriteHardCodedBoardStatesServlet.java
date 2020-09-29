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

/**
 * Servlet for testing the board visualization.
 *
 * Clear all real and estimated board state entities (corresponding to the demo simulation id) from datastore.
 *
 * Generate 10 random real boards and 10 random estimated boards in a demo 5*5 simulation,
 * by locating dummy beacons in random locations.
 *
 * Write generated board state to datastore.
 *
 */
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

        // Clear datastore.
        String simulationId = "demo-tracing-sim-1";
        datastore.deleteAllSimulationRealBoardStates(simulationId);
        datastore.deleteAllSimulationEstimatedBoardStates(simulationId);

        for (int round = 0; round < 10; round++) {
            // Clear boards.
            realBoard = new Board(5, 5);
            estimatedBoard = new Board(5, 5);

            // Randomly locate dummy beacons on both boards.
            realBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), firstDummyBeacon);
            realBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), secondDummyBeacon);
            realBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), thirdDummyBeacon);

            estimatedBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), firstDummyBeacon);
            estimatedBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), secondDummyBeacon);
            estimatedBoard.placeAgent(new Location((int) (Math.random() * 3),(int) (Math.random() * 3)), thirdDummyBeacon);

            // Write to datastore.
            datastore.writeRealBoardState(simulationId, round, realBoard.getState());
            datastore.writeEstimatedBoardState(simulationId, round, estimatedBoard.getState());
        }

        response.sendRedirect("/index.html");
    }
}
