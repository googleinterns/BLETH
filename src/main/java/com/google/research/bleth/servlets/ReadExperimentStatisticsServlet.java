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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.research.bleth.simulator.ObservedInterval;
import com.google.research.bleth.simulator.Schema;
import com.google.research.bleth.simulator.SimulationMetadata;
import com.google.research.bleth.simulator.StatisticsState;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for reading experiment's statistical data. */
@WebServlet("/read-experiment-stats")
public class ReadExperimentStatisticsServlet extends HttpServlet {
    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Gson gson = new Gson(); // Used for json serialization.

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImmutableMap.Builder<String, JsonElement> res = new ImmutableMap.Builder<>();
        String experimentId = request.getParameter("experimentId");

        for (String simulationId : retrieveSimulations(experimentId)) {
            ImmutableMap.Builder<String, JsonElement> simulationJson = new ImmutableMap.Builder<>(); // Stores metadata and stats.
            SimulationMetadata metadata = SimulationMetadata.read(simulationId);
            simulationJson.put(Schema.SimulationMetadata.entityKind, gson.toJsonTree(metadata));
            Map<String, Double> distancesStats = StatisticsState.readDistancesStats(simulationId);
            simulationJson.put(Schema.StatisticsState.entityKindDistance, gson.toJsonTree(distancesStats));
            ImmutableMultimap<Integer, ObservedInterval> observedIntervalsStats = StatisticsState.readIntervalStats(simulationId);
            simulationJson.put(Schema.StatisticsState.entityKindBeaconsObservedIntervals, serializeObservedIntervalsMap(observedIntervalsStats));
            res.put(simulationId, gson.toJsonTree(simulationJson.build()));
        }

        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(res.build()));
    }

    private static List<String> retrieveSimulations(String experimentId) {
        Query.Filter filterByExperimentId =
                new Query.FilterPredicate(Schema.ExperimentsToSimulations.experimentId,
                        Query.FilterOperator.EQUAL, experimentId);
        Query simulationsQuery = new Query(Schema.ExperimentsToSimulations.entityKind)
                .setFilter(filterByExperimentId);
        return datastore.prepare(simulationsQuery)
                .asList(FetchOptions.Builder.withDefaults())
                .stream()
                .map(entity -> (String) entity.getProperty(Schema.ExperimentsToSimulations.simulationId))
                .collect(Collectors.toList());
    }

    private static JsonElement serializeObservedIntervalsMap(ImmutableMultimap<Integer, ObservedInterval> observedIntervalsMap) {
        ImmutableMap.Builder<Integer, JsonArray> res = new ImmutableMap.Builder<>();
        for (Integer beaconId : observedIntervalsMap.keySet()) {
            List<JsonElement> serializedBeaconIntervals = observedIntervalsMap.get(beaconId).stream()
                    .map(ReadExperimentStatisticsServlet::serializeObservedInterval)
                    .collect(ImmutableList.toImmutableList());
            res.put(beaconId, gson.toJsonTree(serializedBeaconIntervals).getAsJsonArray());
        }
        return gson.toJsonTree(res.build());
    }

    private static JsonElement serializeObservedInterval(ObservedInterval interval) {
        ImmutableMap.Builder<String, Integer> res = new ImmutableMap.Builder<>();
        int observed = interval.observed() ? 1 : -1;
        res.put("start", interval.start());
        res.put("end", interval.end());
        res.put("duration", observed * interval.duration()); // positive duration iff observed.
        return gson.toJsonTree(res.build());
    }
}
