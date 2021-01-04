// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.research.bleth.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.research.bleth.simulator.AbstractSimulation;
import com.google.research.bleth.simulator.AwakenessStrategyFactory;
import com.google.research.bleth.simulator.MovementStrategyFactory;
import com.google.research.bleth.simulator.Schema;
import com.google.research.bleth.simulator.StrategiesMapper;
import com.google.research.bleth.simulator.TracingSimulation;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet used for creating and running a new simulation associated to an experiment,
 * and updating experimentsToSimulations database entity (which maps experiments to their associated simulations).
 */
@WebServlet("/new-experiment-simulation")
public class NewExperimentSimulationServlet extends HttpServlet {
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Logger log = Logger.getLogger(NewExperimentSimulationServlet.class.getName());

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        log.info("Experiment Simulation Worker Servlet invoked.");
        log.info("Enqueue Experiment Servlet received an http POST request with params: "
        + request.getParameterMap().toString());

        // Get request parameters.
        String experimentId = request.getParameter("experimentId");
        String simulationDescription = request.getParameter("description");
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
        response.setContentType("text/plain;");
        try {
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

            simulation.run();
            updateExperiment(simulation.getId(), experimentId);
        } catch (RuntimeException | EntityNotFoundException e) {
            responseText = e.getMessage();
        }
        response.getWriter().println(responseText);
    }

    private void updateExperiment(String simulationId, String experimentId) throws EntityNotFoundException {
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction transaction = datastore.beginTransaction(options);
        try {
            // update simulationsToExperiments
            Entity experimentToSimulation = new Entity((Schema.ExperimentsToSimulations.entityKind));
            experimentToSimulation.setProperty(Schema.ExperimentsToSimulations.experimentId, experimentId);
            experimentToSimulation.setProperty(Schema.ExperimentsToSimulations.simulationId, simulationId);
            datastore.put(experimentToSimulation);

            // decrease experiment counter
            Entity experiment = datastore.get(KeyFactory.stringToKey(experimentId));
            int simulationsLeft = ((Long) experiment.getProperty(Schema.Experiment.simulationsLeft)).intValue();
            experiment.setProperty(Schema.Experiment.simulationsLeft, simulationsLeft - 1);
            datastore.put(experiment);
            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                log.info("Transaction has been rolled back.");
                transaction.rollback();
            }
        }
    }
}
