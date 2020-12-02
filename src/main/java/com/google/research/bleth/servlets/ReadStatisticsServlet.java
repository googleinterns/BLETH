package com.google.research.bleth.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.research.bleth.simulator.Schema;
import com.google.research.bleth.simulator.StatisticsState;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for reading simulations' statistical data. */
@WebServlet("/read-stats")
public class ReadStatisticsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson(); // Used for json serialization.

        String simulationId = request.getParameter("simulationId");
        HashMap<String, JsonElement> statistics = new HashMap<>();

        // Read both kinds of statistics and store it in a serialized hash map.
        Map<String, Double> beaconsObservedPercentStats = StatisticsState.readBeaconsObservedPercentStats(simulationId);
        Map<String, Double> distancesStats = StatisticsState.readDistancesStats(simulationId);
        statistics.put(Schema.StatisticsState.entityKindBeaconsObservedPercent, gson.toJsonTree(beaconsObservedPercentStats));
        statistics.put(Schema.StatisticsState.entityKindDistance, gson.toJsonTree(distancesStats));

        // Write to response.
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(statistics));
    }
}
