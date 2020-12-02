package com.google.research.bleth.servlets;

import com.google.research.bleth.simulator.AbstractSimulation;
import com.google.research.bleth.simulator.AwakenessStrategyFactory;
import com.google.research.bleth.simulator.MovementStrategyFactory;
import com.google.research.bleth.simulator.StrategiesMapper;
import com.google.research.bleth.simulator.TracingSimulation;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for creating and running a new simulation. */
@WebServlet("/new-simulation")
public class NewSimulationServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get request parameters.
        String simulationDescription = request.getParameter("simulationDescription");
        int roundsNum = Integer.parseInt(request.getParameter("roundsNum"));
        int rowsNum = Integer.parseInt(request.getParameter("rowsNum"));
        int colsNum = Integer.parseInt(request.getParameter("colsNum"));
        int beaconsNum = Integer.parseInt(request.getParameter("beaconsNum"));
        int observersNum = Integer.parseInt(request.getParameter("observersNum"));
        String beaconMovementStrategyAsString = request.getParameter("beaconMovementStrategy");
        String observerMovementStrategyAsString = request.getParameter("observerMovementStrategy");
        String observerAwakenessStrategyAsString = request.getParameter("observerAwakenessStrategy");
        int awakenessCycle = Integer.parseInt(request.getParameter("awakenessCycle"));
        int awakenessDuration = Integer.parseInt(request.getParameter("awakenessDuration"));
        double transmissionThresholdRadius = Double.parseDouble(request.getParameter("transmissionThresholdRadius"));

        // Mapping strategies string to types.
        StrategiesMapper strategiesMapper = StrategiesMapper.getInstance();
        MovementStrategyFactory.Type beaconMovementStrategy = strategiesMapper.getMovementStrategy(beaconMovementStrategyAsString);
        MovementStrategyFactory.Type observerMovementStrategy = strategiesMapper.getMovementStrategy(observerMovementStrategyAsString);
        AwakenessStrategyFactory.Type observerAwakenessStrategy = strategiesMapper.getAwakenessStrategy(observerAwakenessStrategyAsString);

        String responseText = "Simulation has been created successfully.";
        try {
            // Create a new simulation.
            AbstractSimulation simulation = new TracingSimulation.Builder()
                    .setDescription(simulationDescription)
                    .setMaxNumberOfRounds(roundsNum)
                    .setRowNum(rowsNum)
                    .setColNum(colsNum)
                    .setBeaconsNum(beaconsNum)
                    .setObserversNum(observersNum)
                    .setBeaconMovementStrategyType(beaconMovementStrategy)
                    .setObserverMovementStrategyType(observerMovementStrategy)
                    .setAwakenessStrategyType(observerAwakenessStrategy)
                    .setAwakenessCycle(awakenessCycle)
                    .setAwakenessDuration(awakenessDuration)
                    .setTransmissionThresholdRadius(transmissionThresholdRadius)
                    .build();

            // Run simulation.
            simulation.run();
        } catch (Exception e) {
            responseText = e.getMessage();
        }

        // Write to response.
        response.setContentType("text/html;");
        response.getWriter().println(responseText);
    }
}
