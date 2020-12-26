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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.auto.value.AutoValue;
import com.google.cloud.tasks.v2.AppEngineHttpRequest;
import com.google.cloud.tasks.v2.CloudTasksClient;
import com.google.cloud.tasks.v2.HttpMethod;
import com.google.cloud.tasks.v2.QueueName;
import com.google.cloud.tasks.v2.Task;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.research.bleth.simulator.AwakenessStrategyFactory;
import com.google.research.bleth.simulator.MovementStrategyFactory;
import com.google.research.bleth.simulator.Schema;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet used for enqueuing multiple tasks targeted at endpoint '/new-experiment-simulation',
 * in order to create and run a new experiment.
 */
@WebServlet("/enqueue-experiment")
public class EnqueueExperimentServlet extends HttpServlet {
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Gson gson = new Gson();
    private static final MovementStrategyFactory.Type defaultMovementStrategy = MovementStrategyFactory.Type.RANDOM;
    private static final AwakenessStrategyFactory.Type defaultAwakenessStrategy = AwakenessStrategyFactory.Type.RANDOM;
    private static final String PROJECT_ID = "bleth-2020";
    private static final String LOCATION_ID = "europe-west1";
    private static final String QUEUE_ID = "simulations-queue";

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if (request.getServerName().equals("localhost") && request.getServerPort() == 8080) {
            response.setContentType("text/plain;");
            response.getWriter().println("Experiments are not yet supported on localhost.");
            return;
        }

        String experimentTitle = request.getParameter("experimentTitle");
        int beaconsNum = Integer.parseInt(request.getParameter("beaconsNum"));
        Set<List<PropertyWrapper>> configurations = createConfigurations(request);

        Entity experiment = new Entity(Schema.Experiment.entityKind);
        experiment.setProperty(Schema.Experiment.experimentTitle, experimentTitle);
        Key experimentId = datastore.put(experiment);

        int legalConfigurationsCount = 0;
        for (List<PropertyWrapper> configuration : configurations) {
            if (validateArguments(configuration)) {
                AppEngineHttpRequest httpRequest = toHttpRequest(configuration, beaconsNum, experimentId);
                enqueueTask(httpRequest);
                legalConfigurationsCount++;
            }
        }

        response.setContentType("text/plain;");
        response.getWriter().println("All tasks have been added to queue.");
    }

    @AutoValue
    public static abstract class PropertyWrapper {
        public static PropertyWrapper create(String property, Number value) {
            return new AutoValue_EnqueueExperimentServlet_PropertyWrapper(property, value);
        }
        public abstract String property();
        public abstract Number value();
    }

    private Set<List<PropertyWrapper>> createConfigurations(HttpServletRequest request) {
        Set<PropertyWrapper> roundsNumValues = createIntValues(request, "roundsNum");
        Set<PropertyWrapper> rowsNumValues = createIntValues(request, "rowsNum");
        Set<PropertyWrapper> colsNumValues = createIntValues(request, "colsNum");
        Set<PropertyWrapper> observersNumValues = createIntValues(request, "observersNum");
        Set<PropertyWrapper> awakenessCycleValues = createIntValues(request, "awakenessCycle");
        Set<PropertyWrapper> awakenessDurationValues = createIntValues(request, "awakenessDuration");
        Set<PropertyWrapper> transmissionThresholdRadiusValues = createDoubleValues(request, "transmissionThresholdRadius");

        return Sets.cartesianProduct(
                roundsNumValues,
                rowsNumValues,
                colsNumValues,
                observersNumValues,
                awakenessCycleValues,
                awakenessDurationValues,
                transmissionThresholdRadiusValues
        );
    }

    private Set<PropertyWrapper> createIntValues(HttpServletRequest request, String parameter) {
        int lower = Integer.parseInt(request.getParameter("lower" + upperCaseFirstChar(parameter)));
        int upper = Integer.parseInt(request.getParameter("upper" + upperCaseFirstChar(parameter)));
        int step = Integer.parseInt(request.getParameter("step" + upperCaseFirstChar(parameter)));
        Set<PropertyWrapper> values = new HashSet<>();
        for (int value = lower; value <= upper; value += step) {
            values.add(PropertyWrapper.create(parameter, value));
        }
        return ImmutableSet.copyOf(values);
    }

    private Set<PropertyWrapper> createDoubleValues(HttpServletRequest request, String parameter) {
        double lower = Double.parseDouble(request.getParameter("lower" + upperCaseFirstChar(parameter)));
        double upper = Double.parseDouble(request.getParameter("upper" + upperCaseFirstChar(parameter)));
        double step = Double.parseDouble(request.getParameter("step" + upperCaseFirstChar(parameter)));
        Set<PropertyWrapper> values = new HashSet<>();
        for (double value = lower; value <= upper; value += step) {
            values.add(PropertyWrapper.create(parameter, value));
        }
        return ImmutableSet.copyOf(values);
    }

    private String upperCaseFirstChar(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private AppEngineHttpRequest toHttpRequest(List<PropertyWrapper> configuration,
                                               int beaconsNum, Key experimentId) {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put(Schema.SimulationMetadata.beaconsNum, String.valueOf(beaconsNum));
        bodyMap.put(Schema.SimulationMetadata.beaconMovementStrategy, defaultMovementStrategy.toString());
        bodyMap.put(Schema.SimulationMetadata.observerMovementStrategy, defaultMovementStrategy.toString());
        bodyMap.put(Schema.SimulationMetadata.observerAwakenessStrategy, defaultAwakenessStrategy.toString());
        bodyMap.put(Schema.ExperimentsToSimulations.experimentId, KeyFactory.keyToString(experimentId));
        configuration.forEach((propertyWrapper -> {
            bodyMap.put(propertyWrapper.property(), String.valueOf(propertyWrapper.value()));
        }));

        return AppEngineHttpRequest.newBuilder()
                .setRelativeUri("/new-experiment-simulation")
                .setHttpMethod(HttpMethod.POST)
                .putHeaders("Content-Type", "application/json;")
                .setBody(ByteString.copyFromUtf8(gson.toJson(bodyMap)))
                .build();
    }

    private boolean validateArguments(List<PropertyWrapper> configuration) {
        Map<String, Number> properties = configuration.stream()
                .collect(Collectors.toMap(PropertyWrapper::property, PropertyWrapper::value));

        boolean nonPositiveDimensions = properties.get(Schema.SimulationMetadata.rowsNum).intValue() <= 0 ||
                properties.get(Schema.SimulationMetadata.rowsNum).intValue() <= 0;
        boolean nonPositiveAgentsNumber = properties.get(Schema.SimulationMetadata.beaconsNum).intValue() <= 0 ||
                properties.get(Schema.SimulationMetadata.observersNum).intValue() <= 0;
        boolean nonPositiveCycleAndDuration = properties.get(Schema.SimulationMetadata.awakenessCycle).intValue() <= 0 ||
                properties.get(Schema.SimulationMetadata.awakenessDuration).intValue() <= 0;
        boolean nonPositiveThresholdRadius = properties.get(Schema.SimulationMetadata.transmissionThresholdRadius).intValue() <= 0;
        boolean nonPositiveRoundsNumber = properties.get(Schema.SimulationMetadata.roundsNum).intValue() <= 0;
        boolean durationGreaterThanCycle = properties.get(Schema.SimulationMetadata.awakenessCycle).intValue() <
                properties.get(Schema.SimulationMetadata.awakenessDuration).intValue();

        return !nonPositiveDimensions && !nonPositiveAgentsNumber && !nonPositiveCycleAndDuration && !nonPositiveThresholdRadius
                && !nonPositiveRoundsNumber && !durationGreaterThanCycle;
    }

    private void enqueueTask(AppEngineHttpRequest httpRequest) throws IOException {
        try (CloudTasksClient client = CloudTasksClient.create()) {
            Task task = Task.newBuilder()
                    .setAppEngineHttpRequest(httpRequest)
                    .build();
            String queueName = QueueName.of(PROJECT_ID, LOCATION_ID, QUEUE_ID).toString();
            client.createTask(queueName, task);
        }
    }
}
