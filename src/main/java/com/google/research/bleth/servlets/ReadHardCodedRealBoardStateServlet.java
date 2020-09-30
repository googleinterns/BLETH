package com.google.research.bleth.servlets;

import com.google.research.bleth.services.BoardParser;
import com.google.research.bleth.services.DatabaseService;
import com.google.research.bleth.simulator.Board;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet for testing the board visualization.
 * Writes to response the estimated board state corresponding to a round of a demo hard coded simulation.
 * round is a request parameter.
 */
@WebServlet("/read-hard-coded-real-board-state")
public class ReadHardCodedRealBoardStateServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");

        int round = Integer.parseInt(request.getParameter("round"));
        String simulationId = "demo-tracing-sim-1";

        // If board state does not exists in datastore, write the state of an empty 5*5 board.
        String boardState = DatabaseService.getInstance().getRealBoardState(simulationId, round);
        if (boardState != null) {
            response.getWriter().println(boardState);
        } else {
            response.getWriter().println(BoardParser.parse(new Board(5, 5)));
        }
    }
}
