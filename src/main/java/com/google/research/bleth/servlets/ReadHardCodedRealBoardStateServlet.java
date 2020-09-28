package com.google.research.bleth.servlets;

import com.google.research.bleth.services.DatabaseService;
import com.google.research.bleth.simulator.Board;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/read-hard-coded-real-board-state")
public class ReadHardCodedRealBoardStateServlet extends HttpServlet {

    private Board board;

    @Override
    public void init() {

    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;");
        String randomBoardState = DatabaseService.getInstance().getRandomRealBoardState();
        response.getWriter().println(randomBoardState);
    }
}
