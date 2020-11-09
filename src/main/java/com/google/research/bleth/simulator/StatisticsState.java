package com.google.research.bleth.simulator;

import com.google.appengine.api.datastore.*;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class StatisticsState {
    private String simulationId;
    private Map<String, Double> distanceStats;
    private Map<String, Double> beaconsObserved;

    /**
     *
     * @param distanceStats
     * @param beaconsObserved
     * @return
     */
    public static StatisticsState create(String simulationId, Map<String, Double> distanceStats, Map<String, Double> beaconsObserved) {
        checkNotNull(distanceStats);
        checkNotNull(beaconsObserved);
        return new StatisticsState(simulationId, distanceStats, beaconsObserved);
    }

    /**
     *
     */
    public void writeDistancesStats() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        for (Map.Entry<String, Double> entry : distanceStats.entrySet()) {
            Entity entity = new Entity(Schema.StatisticsState.entityKindDistance);
            entity.setProperty(Schema.StatisticsState.simulationId, simulationId);
            entity.setProperty(Schema.StatisticsState.aggregateFunction, entry.getKey());
            entity.setProperty(Schema.StatisticsState.value, entry.getValue());
            datastore.put(entity);
        }
    }

    public void writeBeaconsObservedPercentStats() {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        for (String beaconId : beaconsObserved.keySet()) {
            Entity entity = new Entity(Schema.StatisticsState.entityKindObservedPercent);
            entity.setProperty(Schema.StatisticsState.simulationId, simulationId);
            entity.setProperty(Schema.StatisticsState.beaconId, beaconId);
            entity.setProperty(Schema.StatisticsState.percent, beaconsObserved.get(beaconId));
            datastore.put(entity);
        }
    }

    /**
     *
     */
    public static Map<String, Double> readDistancesStats(String simulationId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Map<String, Double> distanceStats = new HashMap<>();

        Query.FilterPredicate filterBySimulationId =
                new Query.FilterPredicate(Schema.StatisticsState.simulationId, Query.FilterOperator.EQUAL, simulationId);
        PreparedQuery distances = datastore.prepare(new Query(Schema.StatisticsState.entityKindDistance).setFilter(filterBySimulationId));

        for (Entity entity : distances.asIterable()) {
            String kind = (String) entity.getProperty(Schema.StatisticsState.aggregateFunction);
            Double value = (Double) entity.getProperty(Schema.StatisticsState.value);
            distanceStats.put(kind, value);
        }
        return distanceStats;
    }

    /**
     *
     */
    public static Map<String, Double> readBeaconsObservedPercentStats(String simulationId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Map<String, Double> beaconsObserved = new HashMap<>();

        Query.FilterPredicate filterBySimulationId =
                new Query.FilterPredicate(Schema.StatisticsState.simulationId, Query.FilterOperator.EQUAL, simulationId);
        PreparedQuery percents = datastore.prepare(new Query(Schema.StatisticsState.entityKindObservedPercent).setFilter(filterBySimulationId));

        for (Entity entity : percents.asIterable()) {
            String beaconId = (String) entity.getProperty(Schema.StatisticsState.beaconId);
            Double percent = (Double) entity.getProperty(Schema.StatisticsState.percent);
            beaconsObserved.put(beaconId, percent);
        }
        return beaconsObserved;
    }

    private StatisticsState(String simulationId, Map<String, Double> distanceStats, Map<String, Double> beaconsObserved) {
        this.simulationId = simulationId;
        this.distanceStats = distanceStats;
        this.beaconsObserved = beaconsObserved;
    }
}
