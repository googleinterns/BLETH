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

package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Table;
import com.google.research.bleth.exceptions.StatisticsAlreadyExistException;
import java.util.Map;

/** A mediator between the statistics the simulation gathered and their storage on datastore. */
public class StatisticsState {
    private final String simulationId;
    private final Map<String, Double> distanceStats;
    private final Table<String, String, Double> beaconsObservedStats;
    private final LinkedListMultimap<Integer, ObservedInterval> beaconsObservedIntervals;
    private static final String OBSERVED = "OBSERVED";
    private static final String NOT_OBSERVED = "NOT_OBSERVED";

    /**
     * Create a new StatisticsState.
     * @param simulationId is the simulation id associated with the statistical data.
     * @param distanceStats is statistics about the difference between the beacons' real locations and their estimated locations.
     * @return a new instance of StatisticsState
     */
    public static StatisticsState create(String simulationId, Map<String, Double> distanceStats,
                                         Table<String, String, Double> beaconsObservedStats,
                                         LinkedListMultimap<Integer, ObservedInterval> beaconsObservedIntervals) {
        checkNotNull(distanceStats);
        checkNotNull(beaconsObservedStats);
        checkNotNull(beaconsObservedIntervals);
        return new StatisticsState(simulationId, ImmutableMap.copyOf(distanceStats),
                   beaconsObservedStats, beaconsObservedIntervals);
    }

    /** Create and write datastore entities storing beacons' observed intervals. */
    public void writeIntervalStats() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        for (Integer beaconId : beaconsObservedIntervals.keySet()) {
            for (ObservedInterval interval : beaconsObservedIntervals.get(beaconId)) {
                Entity entity = new Entity(Schema.StatisticsState.entityKindBeaconsObservedIntervals);
                entity.setProperty(Schema.StatisticsState.simulationId, simulationId);
                entity.setProperty(Schema.StatisticsState.beaconId, beaconId);
                entity.setProperty(Schema.StatisticsState.intervalStart, interval.start());
                entity.setProperty(Schema.StatisticsState.intervalEnd, interval.end());
                entity.setProperty(Schema.StatisticsState.intervalObserved, interval.observed() ? OBSERVED : NOT_OBSERVED);
                datastore.put(entity);
            }
        }
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
     * Create and write multiple datastore entities, represent statistics about the intervals of time each beacon
     * has been observed by at least one observer, i.e. has been detected by the resolver.
     * @throws StatisticsAlreadyExistException if the simulation's observed statistics are already exists in the database.
     */
    public void writeBeaconsObservedStats() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Table<String, String, Double> beaconsIdToObservedStats = readBeaconsObservedStats(simulationId);
        if (!beaconsIdToObservedStats.isEmpty()) {
            throw new StatisticsAlreadyExistException(simulationId);
        }

        for (String beaconId : beaconsObservedStats.rowKeySet()) {
            Entity entity = new Entity(Schema.StatisticsState.entityKindBeaconsObserved);
            entity.setProperty(Schema.StatisticsState.simulationId, simulationId);
            entity.setProperty(Schema.StatisticsState.beaconId, beaconId);
            beaconsObservedStats.row(beaconId).forEach(entity::setProperty);
            datastore.put(entity);
        }
    }

    /**
     * Read from the db beacons' observed intervals.
     * @param simulationId is the simulation id.
     * @return an immutable multimap storing all beacons' observed intervals.
     */
    public static ImmutableMultimap<Integer, ObservedInterval> readIntervalStats(String simulationId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        ImmutableListMultimap.Builder<Integer, ObservedInterval> beaconsObservedIntervals = ImmutableListMultimap.builder();
        int beaconsNum = SimulationMetadata.read(simulationId).beaconsNum;

        for (int beaconId = 0; beaconId < beaconsNum; beaconId++) {
            Query intervalsQuery = new Query(Schema.StatisticsState.entityKindBeaconsObservedIntervals);
            Query.Filter filterByBeaconId = new Query.FilterPredicate(Schema.StatisticsState.beaconId,
                    Query.FilterOperator.EQUAL, beaconId);
            Query.Filter filterBySimulationId = new Query.FilterPredicate(Schema.StatisticsState.simulationId,
                    Query.FilterOperator.EQUAL, simulationId);
            Query.CompositeFilter composedQueryFilter = Query.CompositeFilterOperator.and(filterByBeaconId, filterBySimulationId);
            intervalsQuery.setFilter(composedQueryFilter);
            intervalsQuery.addSort(Schema.StatisticsState.intervalStart, Query.SortDirection.ASCENDING);
            PreparedQuery intervalsPreparedQuery = datastore.prepare(intervalsQuery);
            for (Entity entity : intervalsPreparedQuery.asIterable()) {
                ObservedInterval interval = extractObservedInterval(entity);
                beaconsObservedIntervals.put(beaconId, interval);
            }
        }
        return beaconsObservedIntervals.build();
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
     * Read from the database statical data about the intervals of time each beacon has been observed.
     * @param simulationId is the simulation id associated with the statistical data.
     * @return a table that maps a beacon's id to its observation statistics during the simulation.
     */
    public static Table<String, String, Double> readBeaconsObservedStats(String simulationId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        Query.FilterPredicate filterBySimulationId =
                new Query.FilterPredicate(Schema.StatisticsState.simulationId, Query.FilterOperator.EQUAL, simulationId);
        Query observedStats = new Query(Schema.StatisticsState.entityKindBeaconsObserved).setFilter(filterBySimulationId);
        PreparedQuery observedStatisticsEntities = datastore.prepare(observedStats);

        ImmutableTable.Builder<String, String, Double> observedStatistics = new ImmutableTable.Builder<>();
        for (Entity entity : observedStatisticsEntities.asIterable()) {
            entity.getProperties().entrySet().stream()
            .filter(entry -> !(entry.getKey().equals(Schema.StatisticsState.simulationId)
                               || (entry.getKey().equals(Schema.StatisticsState.beaconId))))
            .forEach(entry -> observedStatistics.put((String) entity.getProperty(Schema.StatisticsState.beaconId),
                                                     entry.getKey(), (Double) entry.getValue()));
        }
        return observedStatistics.build();
    }

    private StatisticsState(String simulationId, Map<String, Double> distanceStats,
                            Table<String, String, Double> beaconsObservedStats,
                            LinkedListMultimap<Integer, ObservedInterval> beaconsObservedIntervals) {
        this.simulationId = simulationId;
        this.distanceStats = distanceStats;
        this.beaconsObservedStats = beaconsObservedStats;
        this.beaconsObservedIntervals = beaconsObservedIntervals;
    }

    private static ObservedInterval extractObservedInterval(Entity entity) {
        int start = ((Long) entity.getProperty(Schema.StatisticsState.intervalStart)).intValue();
        int end = ((Long) entity.getProperty(Schema.StatisticsState.intervalEnd)).intValue();
        boolean observed = entity.getProperty(Schema.StatisticsState.intervalObserved).equals(OBSERVED);
        return new AutoValue_ObservedInterval.Builder()
                .setObserved(observed)
                .setStart(start)
                .setEnd(end)
                .build();
    }
}
