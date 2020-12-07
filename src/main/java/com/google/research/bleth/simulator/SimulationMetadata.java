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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.ImmutableMap;

/** A class for storing, reading and writing simulation metadata. */
public class SimulationMetadata {
    public final String description;
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
        this.description = builder.getDescription();
        this.roundsNum = builder.getMaxNumberOfRounds();
        this.beaconsNum = builder.getBeaconsNum();
        this.observersNum = builder.getObserversNum();
        this.rowsNum = builder.getRowNum();
        this.colsNum = builder.getColNum();
        this.beaconMovementStrategy = builder.getBeaconMovementStrategyType().toString();
        this.observerMovementStrategy = builder.getObserverMovementStrategyType().toString();
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
        entity.setProperty(Schema.SimulationMetadata.description, this.description);
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

    /**
     * Read all existing SimulationMetadata entites from the db, and return them as a hashmap.
     * @return a hashmap mapping a simulationId to the corresponding SimulationMetadata object.
     */
    public static ImmutableMap<String, SimulationMetadata> listSimulations() {
        ImmutableMap.Builder<String, SimulationMetadata> simulations = new ImmutableMap.Builder<>();
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query simulationMetadataQuery = new Query(Schema.SimulationMetadata.entityKind);
        PreparedQuery simulationMetadataPreparedQuery = datastore.prepare(simulationMetadataQuery);
        for (Entity entity : simulationMetadataPreparedQuery.asIterable()) {
            simulations.put(KeyFactory.keyToString(entity.getKey()), new SimulationMetadata(entity));
        }
        return simulations.build();
    }

    /** Return true if provided round exists in the simulation associated with the provided simulation id, and false otherwise. */
    static boolean isRoundExistsInSimulation(String simulationId, int round) {
        SimulationMetadata simulationMetadata = read(simulationId);
        int maxSimulationRound = simulationMetadata.roundsNum;
        return round < maxSimulationRound;
    }

    private SimulationMetadata(Entity entity) {
        this.description = (String) entity.getProperty(Schema.SimulationMetadata.description);
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
