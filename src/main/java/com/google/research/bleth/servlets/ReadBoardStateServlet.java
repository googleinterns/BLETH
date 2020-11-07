package com.google.research.bleth.servlets;

import com.google.research.bleth.simulator.BoardState;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for reading a board state from the db and write it to response as a JSON 2D array. */
@WebServlet("/read-board-state")
public class ReadBoardStateServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get board state parameters.
        int round = Integer.parseInt(request.getParameter("round"));
        String simulationId = request.getParameter("simulationId");
        boolean isReal = Boolean.parseBoolean(request.getParameter("isReal"));

        // Read board state.
        BoardState boardState;
        if (isReal) {
            boardState = BoardState.readReal(simulationId, round);
        } else {
            boardState = BoardState.readEstimated(simulationId, round);
        }

        // Write to response.
        response.setContentType("application/json;");
        response.getWriter().println(boardState.toJson());
    }
}
