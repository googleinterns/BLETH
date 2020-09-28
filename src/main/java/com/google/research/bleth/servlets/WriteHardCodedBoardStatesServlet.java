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

@WebServlet("/hard-coded-board-state-write")
public class WriteHardCodedBoardStatesServlet extends HttpServlet {

    DatabaseService datastore = DatabaseService.getInstance();
    private Board realBoard = new Board(5, 5);;
    private Board estimatedBoard = new Board(5, 5);;

    @Override
    public void init() {
        DummyBeacon firstDummyBeacon = new DummyBeacon(0, new Location(1,1));
        DummyBeacon secondDummyBeacon = new DummyBeacon(1, new Location(2,2));
        DummyBeacon thirdDummyBeacon = new DummyBeacon(2, new Location(3,4));

        realBoard.placeAgent(firstDummyBeacon.moveTo(), firstDummyBeacon);
        realBoard.placeAgent(secondDummyBeacon.moveTo(), secondDummyBeacon);
        realBoard.placeAgent(thirdDummyBeacon.moveTo(), thirdDummyBeacon);

        estimatedBoard.placeAgent(new Location(1,1), firstDummyBeacon);
        estimatedBoard.placeAgent(new Location(1,2), secondDummyBeacon);
        estimatedBoard.placeAgent(new Location(3,3), thirdDummyBeacon);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String simulationId = request.getParameter("simulationId");
        String round = request.getParameter("round");
        datastore.writeRealBoardState(simulationId, round, realBoard.getState());
        datastore.writeEstimatedBoardState(simulationId, round, estimatedBoard.getState());

        response.sendRedirect("/index.html");
    }
}
