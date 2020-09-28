package com.google.research.bleth.servlets;

import com.google.research.bleth.simulator.Board;
import com.google.research.bleth.simulator.DummyBeacon;
import com.google.research.bleth.simulator.Location;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/hard-coded-board-state")
public class HardCodedBoardStateServlet extends HttpServlet {

    private Board board;

    @Override
    public void init() {
        board = new Board(5, 5);
        DummyBeacon firstDummyBeacon = new DummyBeacon(0, new Location(1,1));
        DummyBeacon secondDummyBeacon = new DummyBeacon(1, new Location(2,2));
        DummyBeacon thirdDummyBeacon = new DummyBeacon(2, new Location(3,4));
        board.placeAgent(firstDummyBeacon.moveTo(), firstDummyBeacon);
        board.placeAgent(secondDummyBeacon.moveTo(), secondDummyBeacon);
        board.placeAgent(thirdDummyBeacon.moveTo(), thirdDummyBeacon);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");
        response.getWriter().println(board.getState());
    }
}
