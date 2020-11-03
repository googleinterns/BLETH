package com.google.research.bleth.simulator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/** A class for storing, reading and writing simulation metadata. */
public class SimulationMetadata {
    public final String type;
    public final int roundsNum;
    public final int beaconsNum;
    public final int observersNum;
    public final int rowsNum;
    public final int colsNum;
    public final String beaconMovementStrategy;
    public final String observerMovementStrategy;
    public final String observerAwakenessStrategy;
    public final double transmissionThresholdRadius;
    public final int awakenessCycle;
    public final int awakenessDuration;
    public final double observersDensity;
    public final double awakenessRatio;

    /**
     * Create a new simulation metadata object based on a simulation builder.
     * @param builder is the simulation builder storing the simulation metadata.
     */
    public SimulationMetadata(AbstractSimulation.Builder builder) {
        this.type = builder.getSimulationType();
        this.roundsNum = builder.getMaxNumberOfRounds();
        this.beaconsNum = builder.getBeaconsNum();
        this.observersNum = builder.getObserversNum();
        this.rowsNum = builder.getRowNum();
        this.colsNum = builder.getColNum();
        this.beaconMovementStrategy = builder.getBeaconMovementStrategy().toString();
        this.observerMovementStrategy = builder.getObserverMovementStrategy().toString();
        this.observerAwakenessStrategy = builder.getAwakenessStrategyType().toString();
        this.transmissionThresholdRadius = builder.getTransmissionThresholdRadius();
        this.awakenessCycle = builder.getAwakenessCycle();
        this.awakenessDuration = builder.getAwakenessDuration();
        this.observersDensity = (double) builder.getObserversNum() / (builder.getRowNum() * builder.getColNum());
        this.awakenessRatio = (double) builder.getAwakenessDuration() / builder.getAwakenessCycle();
    }

    /**
     * Create a datastore entity representing the simulation metadata and write it to db.
     * @return the unique Id assigned to the datastore entity as a string.
     */
    public String write() {
        // Create new entity.
        Entity entity = new Entity(Schema.SimulationMetadata.entityKind);

        // Set properties.
        entity.setProperty(Schema.SimulationMetadata.type, this.type);
        entity.setProperty(Schema.SimulationMetadata.roundsNum, this.roundsNum);
        entity.setProperty(Schema.SimulationMetadata.beaconsNum, this.beaconsNum);
        entity.setProperty(Schema.SimulationMetadata.observersNum, this.observersNum);
        entity.setProperty(Schema.SimulationMetadata.rowsNum, this.rowsNum);
        entity.setProperty(Schema.SimulationMetadata.colsNum, this.colsNum);
        entity.setProperty(Schema.SimulationMetadata.beaconMovementStrategy, this.beaconMovementStrategy);
        entity.setProperty(Schema.SimulationMetadata.observerMovementStrategy, this.observerMovementStrategy);
        entity.setProperty(Schema.SimulationMetadata.observerAwakenessStrategy, this.observerAwakenessStrategy);
        entity.setProperty(Schema.SimulationMetadata.transmissionThresholdRadius, this.transmissionThresholdRadius);
        entity.setProperty(Schema.SimulationMetadata.awakenessCycle, this.awakenessCycle);
        entity.setProperty(Schema.SimulationMetadata.awakenessDuration, this.awakenessDuration);
        entity.setProperty(Schema.SimulationMetadata.observersDensity, this.observersDensity);
        entity.setProperty(Schema.SimulationMetadata.awakenessRatio, this.awakenessRatio);

        // Write to datastore and return key as string.
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        return KeyFactory.keyToString(datastore.put(entity));
    }

    /**
     * Read a SimulationMetadata object storing the metadata of the simulation associated with the provided id.
     * @param simulationId the id of the requested simulation.
     * @return a SimulationMetadata object storing the simulation's metadata.
     */
    public static SimulationMetadata read(String simulationId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key simulationKey = KeyFactory.stringToKey(simulationId);
        Query.Filter filterBySimulationId =
                new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, simulationKey);
        Query simulationIdQuery = new Query(Schema.SimulationMetadata.entityKind).setFilter(filterBySimulationId);
        PreparedQuery simulationIdPreparedQuery = datastore.prepare(simulationIdQuery);
        Entity simulationMetadataEntity = simulationIdPreparedQuery.asSingleEntity();
        return new SimulationMetadata(simulationMetadataEntity);
    }

    /** Return true if provided round exists in the simulation associated with the provided simulation id, and false otherwise. */
    static boolean isRoundExistsInSimulation(String simulationId, int round) {
        SimulationMetadata simulationMetadata = read(simulationId);
        int maxSimulationRound = simulationMetadata.roundsNum;
        return round < maxSimulationRound;
    }

    private SimulationMetadata(Entity entity) {
        this.type = (String) entity.getProperty(Schema.SimulationMetadata.type);
        this.roundsNum = ((Long) entity.getProperty(Schema.SimulationMetadata.roundsNum)).intValue();
        this.beaconsNum = ((Long) entity.getProperty(Schema.SimulationMetadata.beaconsNum)).intValue();
        this.observersNum = ((Long) entity.getProperty(Schema.SimulationMetadata.observersNum)).intValue();
        this.rowsNum = ((Long) entity.getProperty(Schema.SimulationMetadata.rowsNum)).intValue();
        this.colsNum = ((Long) entity.getProperty(Schema.SimulationMetadata.colsNum)).intValue();
        this.beaconMovementStrategy = (String) entity.getProperty(Schema.SimulationMetadata.beaconMovementStrategy);
        this.observerMovementStrategy = (String) entity.getProperty(Schema.SimulationMetadata.observerMovementStrategy);
        this.observerAwakenessStrategy = (String) entity.getProperty(Schema.SimulationMetadata.observerAwakenessStrategy);
        this.transmissionThresholdRadius = (double) entity.getProperty(Schema.SimulationMetadata.transmissionThresholdRadius);
        this.awakenessCycle = ((Long) entity.getProperty(Schema.SimulationMetadata.awakenessCycle)).intValue();
        this.awakenessDuration = ((Long) entity.getProperty(Schema.SimulationMetadata.awakenessDuration)).intValue();
        this.observersDensity = (double) this.observersNum / (this.rowsNum * this.colsNum);
        this.awakenessRatio = (double) this.awakenessDuration / this.awakenessCycle;
    }
}
