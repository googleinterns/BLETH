package com.google.research.bleth.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.research.bleth.simulator.StrategiesMapper;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/list-strategies")
public class ListStrategiesServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson(); // Used for json serialization.

        // Construct a hash map storing 'movement' or 'awakeness' as key,
        // and list of strings representing available strategies as value.
        HashMap<String, JsonArray> strategies = new HashMap<>();
        JsonArray movementStrategies = (JsonArray) gson.toJsonTree(StrategiesMapper.listMovementStrategies());
        JsonArray awakenessStrategies = (JsonArray) gson.toJsonTree(StrategiesMapper.listAwakenessStrategies());
        strategies.put("movement", movementStrategies);
        strategies.put("awakeness", awakenessStrategies);

        // Write hash map to response.
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(strategies));
    }
}
