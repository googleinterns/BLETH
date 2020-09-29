package com.google.research.bleth.servlets;

import com.google.research.bleth.services.DatabaseService;
import com.google.research.bleth.simulator.Board;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/read-hard-coded-estimated-board-state")
public class ReadHardCodedEstimatedBoardStateServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");

        int round = Integer.parseInt(request.getParameter("round"));
        String simulationId = "demo-tracing-sim-1";

        String boardState = DatabaseService.getInstance().getBoardState(simulationId, round, false);
        if (boardState != null) {
            response.getWriter().println(boardState);
        } else {
            response.getWriter().println(new Board(5, 5).getState());
        }
    }
}
