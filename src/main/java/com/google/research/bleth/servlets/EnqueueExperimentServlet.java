// Copyright 2021 Google LLC
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

import static com.google.common.collect.ImmutableSet.toImmutableSet;

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
import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import com.google.research.bleth.simulator.AwakenessStrategyFactory;
import com.google.research.bleth.simulator.MovementStrategyFactory;
import com.google.research.bleth.simulator.Schema;
import com.google.research.bleth.simulator.TracingSimulation;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
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
    private static final MovementStrategyFactory.Type defaultMovementStrategy = MovementStrategyFactory.Type.RANDOM;
    private static final AwakenessStrategyFactory.Type defaultAwakenessStrategy = AwakenessStrategyFactory.Type.RANDOM;
    private static final String PROJECT_ID = "bleth-2020";
    private static final String LOCATION_ID = "europe-west1";
    private static final String QUEUE_ID = "simulations-queue";
    private static final String queueName = QueueName.of(PROJECT_ID, LOCATION_ID, QUEUE_ID).toString();
    private static final Logger log = Logger.getLogger(EnqueueExperimentServlet.class.getName());
    private static final TracingSimulation.Builder validator = new TracingSimulation.Builder();

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
        experiment.setProperty(Schema.Experiment.simulationsLeft, configurations.size());
        Key experimentId = datastore.put(experiment);
        log.info("A new experiment entity with id " + KeyFactory.keyToString(experimentId) +
                " was created and written to db.");

        for (List<PropertyWrapper> configuration : configurations) {
            AppEngineHttpRequest httpRequest = toHttpRequest(configuration, beaconsNum, experimentId, experimentTitle);
            log.info("A new AppEngineHttpRequest was created: " + httpRequest.toString());
            enqueueTask(httpRequest);
            log.info("A new Task was created and enqueued.");
        }

        response.setContentType("text/plain;");
        response.getWriter().println(configurations.size() + " tasks have been added to queue.");
    }

    /**
     * A class designated for storing numerical simulation parameters.
     * PropertyWrapper stores the property name and value as an atomic unit.
     */
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

        Set<List<PropertyWrapper>> configurations = Sets.cartesianProduct(
                roundsNumValues,
                rowsNumValues,
                colsNumValues,
                observersNumValues,
                awakenessCycleValues,
                awakenessDurationValues,
                transmissionThresholdRadiusValues
        );

        return configurations.stream().filter(this::validateArguments).collect(toImmutableSet());
    }

    private Set<PropertyWrapper> createIntValues(HttpServletRequest request, String parameter) {
        int lower = Integer.parseInt(request.getParameter("lower" + capitalizeFirstChar(parameter)));
        int upper = Integer.parseInt(request.getParameter("upper" + capitalizeFirstChar(parameter)));
        int step = Integer.parseInt(request.getParameter("step" + capitalizeFirstChar(parameter)));
        Set<PropertyWrapper> values = new HashSet<>();
        for (int value = lower; value <= upper; value += step) {
            values.add(PropertyWrapper.create(parameter, value));
        }
        return ImmutableSet.copyOf(values);
    }

    private Set<PropertyWrapper> createDoubleValues(HttpServletRequest request, String parameter) {
        double lower = Double.parseDouble(request.getParameter("lower" + capitalizeFirstChar(parameter)));
        double upper = Double.parseDouble(request.getParameter("upper" + capitalizeFirstChar(parameter)));
        double step = Double.parseDouble(request.getParameter("step" + capitalizeFirstChar(parameter)));
        Set<PropertyWrapper> values = new HashSet<>();
        for (double value = lower; value <= upper; value += step) {
            values.add(PropertyWrapper.create(parameter, value));
        }
        return ImmutableSet.copyOf(values);
    }

    private String capitalizeFirstChar(String s) {
        return Ascii.toUpperCase(s.substring(0, 1)) + s.substring(1);
    }

    private AppEngineHttpRequest toHttpRequest(List<PropertyWrapper> configuration,
                                               int beaconsNum, Key experimentId, String experimentTitle) throws UnsupportedEncodingException {
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put(Schema.SimulationMetadata.description,
                "Simulation attached to experiment: " + experimentTitle);
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
                .putHeaders("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8;")
                .setBody(ByteString.copyFromUtf8(toQueryString(bodyMap)))
                .build();
    }

    private String toQueryString(Map<String, String> params) throws UnsupportedEncodingException {
        List<String> keyValuePairs = new ArrayList<>();
        for (String param : params.keySet()) {
            keyValuePairs.add(URLEncoder.encode(param, StandardCharsets.UTF_8.toString()) + "=" +
                    URLEncoder.encode(params.get(param), StandardCharsets.UTF_8.toString()));
        }
        return String.join("&", keyValuePairs);
    }

    private boolean validateArguments(List<PropertyWrapper> configuration) {
        // TODO: refactor TracingSimulation.Builder.validateArguments() to be usable in this context.
        Map<String, Number> properties = configuration.stream()
                .collect(Collectors.toMap(PropertyWrapper::property, PropertyWrapper::value));

        try {
            validator
                    .setMaxNumberOfRounds(properties.get(Schema.SimulationMetadata.roundsNum).intValue())
                    .setRowNum(properties.get(Schema.SimulationMetadata.rowsNum).intValue())
                    .setColNum(properties.get(Schema.SimulationMetadata.colsNum).intValue())
                    .setBeaconsNum(1) // dummy value for validation purpose only.
                    .setObserversNum(properties.get(Schema.SimulationMetadata.observersNum).intValue())
                    .setBeaconMovementStrategyType(defaultMovementStrategy)
                    .setObserverMovementStrategyType(defaultMovementStrategy)
                    .setAwakenessStrategyType(defaultAwakenessStrategy)
                    .setAwakenessCycle(properties.get(Schema.SimulationMetadata.awakenessCycle).intValue())
                    .setAwakenessDuration(properties.get(Schema.SimulationMetadata.awakenessDuration).intValue())
                    .setTransmissionThresholdRadius(properties.get(Schema.SimulationMetadata.transmissionThresholdRadius)
                            .doubleValue())
                    .validateArguments();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void enqueueTask(AppEngineHttpRequest httpRequest) throws IOException {
        try (CloudTasksClient client = CloudTasksClient.create()) {
            Task task = Task.newBuilder()
                    .setAppEngineHttpRequest(httpRequest)
                    .build();

            client.createTask(queueName, task);
        }
    }
}
