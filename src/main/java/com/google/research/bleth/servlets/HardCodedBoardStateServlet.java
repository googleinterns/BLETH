package com.google.research.bleth.servlets;

import com.google.research.bleth.simulator.Board;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/welcome-msg")
public class HardCodedBoardStateServlet extends HttpServlet {

    private Board board;

    @Override
    public void init() {
        // todo: locate beacons on the board (when beacon class is implemented).
        board = new Board(5, 5);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");
        response.getWriter().println(board.getState());
    }
}
