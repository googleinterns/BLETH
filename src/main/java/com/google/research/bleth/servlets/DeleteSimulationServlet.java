package com.google.research.bleth.servlets;

import com.google.research.bleth.utils.Queries;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for deleting simulations. */
@WebServlet("/delete-simulation")
public class DeleteSimulationServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String simulationId = request.getParameter("simulationId");
        Queries.delete(simulationId);
        response.setContentType("text/html;");
        response.getWriter().println("Simulation has been deleted successfully.");
    }
}
