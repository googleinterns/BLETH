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

package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.ImmutableMap;
import com.google.research.bleth.exceptions.StatisticsAlreadyExistException;
import java.util.Map;

/** A mediator between the statistics the simulation gathered and their storage on datastore. */
public class StatisticsState {
    private final String simulationId;
    private final Map<String, Double> distanceStats;
    private final Map<String, Double> beaconsObservedPercent;

    /**
     * Create a new StatisticsState.
     * @param simulationId is the simulation id associated with the statistical data.
     * @param distanceStats is statistics about the difference between the beacons' real locations and their estimated locations.
     * @param beaconsObservedPercent maps each beacon to the percentage of rounds in which it has been observed.
     * @return a new instance of StatisticsState
     */
    public static StatisticsState create(String simulationId, Map<String, Double> distanceStats, Map<String, Double> beaconsObservedPercent) {
        checkNotNull(distanceStats);
        checkNotNull(beaconsObservedPercent);
        return new StatisticsState(simulationId, ImmutableMap.copyOf(distanceStats), ImmutableMap.copyOf(beaconsObservedPercent));
    }

    /**
     * Create and write a datastore entity, represents aggregate functions such as min and max,
     * of the distance between the beacons' real locations and their estimated locations.
     * @throws StatisticsAlreadyExistException if the simulation's distance statistics are already exists in the database.
     */
    public void writeDistancesStats() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Map<String, Double> distanceStatsInDb = readDistancesStats(simulationId);
        if (!distanceStatsInDb.isEmpty()) {
            throw new StatisticsAlreadyExistException(simulationId);
        }

        Entity entity = new Entity(Schema.StatisticsState.entityKindDistance);
        entity.setProperty(Schema.StatisticsState.simulationId, simulationId);
        distanceStats.forEach((key, value) -> entity.setProperty(key, value));
        datastore.put(entity);
    }

    /**
     * Create and write a datastore entity, represents the percentage of rounds a beacon
     * has been observed by at least one observer, i.e. has been detected by the resolver.
     * @throws StatisticsAlreadyExistException if the simulation's distance statistics are already exists in the database.
     */
    public void writeBeaconsObservedPercentStats() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Map<String, Double> beaconsIdToObservedPercent = readBeaconsObservedPercentStats(simulationId);
        if (!beaconsIdToObservedPercent.isEmpty()) {
            throw new StatisticsAlreadyExistException(simulationId);
        }

        Entity entity = new Entity(Schema.StatisticsState.entityKindBeaconsObservedPercent);
        entity.setProperty(Schema.StatisticsState.simulationId, simulationId);
        beaconsObservedPercent.forEach((key, value) -> entity.setProperty(key, value));
        datastore.put(entity);
    }

    /**
     * Read from the db statical data about the distance between the beacons' real locations and their estimated locations.
     * @param simulationId is the simulation id associated with the statistical data.
     * @return a map that maps each aggregate function to its value on the simulation.
     */
    public static Map<String, Double> readDistancesStats(String simulationId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.FilterPredicate filterBySimulationId =
                new Query.FilterPredicate(Schema.StatisticsState.simulationId, Query.FilterOperator.EQUAL, simulationId);
        Query distances = new Query(Schema.StatisticsState.entityKindDistance).setFilter(filterBySimulationId);
        Entity distancesStatistics = datastore.prepare(distances).asSingleEntity();

        if (distancesStatistics == null) {
            return ImmutableMap.of();
        }

        return distancesStatistics.getProperties().entrySet().stream()
                .filter(entry -> !(entry.getKey().equals(Schema.StatisticsState.simulationId)))
                .collect(toImmutableMap(e -> e.getKey(), e -> (Double) e.getValue()));
    }

    /**
     * Read from the db statical data about the percentage of rounds each beacon has been observed.
     * @param simulationId is the simulation id associated with the statistical data.
     * @return a map that maps a beacon's id to the percentage of rounds a beacon has been observed by at least one observer.
     */
    public static Map<String, Double> readBeaconsObservedPercentStats(String simulationId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.FilterPredicate filterBySimulationId =
                new Query.FilterPredicate(Schema.StatisticsState.simulationId, Query.FilterOperator.EQUAL, simulationId);
        Query percents = new Query(Schema.StatisticsState.entityKindBeaconsObservedPercent).setFilter(filterBySimulationId);
        Entity percentsStatistics = datastore.prepare(percents).asSingleEntity();

        if (percentsStatistics == null) {
            return ImmutableMap.of();
        }

        return percentsStatistics.getProperties().entrySet().stream()
                .filter(entry -> !(entry.getKey().equals(Schema.StatisticsState.simulationId)))
                .collect(toImmutableMap(e -> e.getKey(), e -> (Double) e.getValue()));
    }

    private StatisticsState(String simulationId, Map<String, Double> distanceStats, Map<String, Double> beaconsObserved) {
        this.simulationId = simulationId;
        this.distanceStats = distanceStats;
        this.beaconsObservedPercent = beaconsObserved;
    }
}
