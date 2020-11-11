package com.google.research.bleth.servlets;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.research.bleth.simulator.SimulationMetadata;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for displaying simulations. */
@WebServlet("/list-simulations")
public class ListSimulationsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson(); // Used for json serialization.

        // Read SimulationMetadata as a HashMap.
        ImmutableMap<String, SimulationMetadata> simulations = SimulationMetadata.listSimulations();

        // Serialize SimulationMetadata objects to JSON strings.
        HashMap<String, JsonElement> simulationsAsJson = new HashMap<>();
        simulations.forEach((id, metadata) -> simulationsAsJson.put(id, gson.toJsonTree(metadata)));

        // Write hash map to response.
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(simulationsAsJson));
    }
}
