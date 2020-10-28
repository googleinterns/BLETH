package com.google.research.bleth.simulator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

/** A placeholder class for representing a BLETH simulation. */
public abstract class AbstractSimulation {

    /**
     * Read an entity storing the metadata of the simulation associated with the provided id.
     * @param simulationId the id of the requested simulation.
     * @return an entity storing the simulation's metadata.
     */
    static Entity readMetadataEntity(String simulationId) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Key simulationKey = KeyFactory.stringToKey(simulationId);
        Query.Filter filterBySimulationId =
                new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, simulationKey);
        Query simulationIdQuery = new Query(Schema.SimulationMetadata.entityKind).setFilter(filterBySimulationId);
        PreparedQuery simulationIdPreparedQuery = datastore.prepare(simulationIdQuery);
        return simulationIdPreparedQuery.asSingleEntity();
    }

    /** Return true if provided round exists in the simulation associated with the provided simulation id, and false otherwise. */
    static boolean roundExistsInSimulation(String simulationId, int round) {
        Entity simulationEntity = readMetadataEntity(simulationId);
        int maxSimulationRound = ((Long) simulationEntity.getProperty(Schema.SimulationMetadata.roundsNum)).intValue();
        return round < maxSimulationRound;
    }

    /** A placeholder class for representing a BLETH simulation builder. */
    public static abstract class Builder {

        public abstract String getSimulationType();

        public int getMaxNumberOfRounds() { return 1; }

        public int getRowNum() { return 1; }

        public int getColNum() { return 1; }

        public int getBeaconsNum() { return 1; }

        public int getObserversNum() { return 1; }

        public IMovementStrategy getBeaconMovementStrategy() { return null; }

        public IMovementStrategy getObserverMovementStrategy() { return null; }

        public AwakenessStrategyFactory.Type getAwakenessStrategyType() { return null; }

        public double getRadius() { return 1; }

        public int getAwakenessCycle() { return 1; }

        public int getAwakenessDuration() { return 1; }

        /** Create an entity storing the simulation's metadata and store it on db. */
        String writeMetadata() {
            // Create new entity.
            Entity entity = new Entity(Schema.SimulationMetadata.entityKind);

            // Set properties.
            entity.setProperty(Schema.SimulationMetadata.type, this.getSimulationType());
            entity.setProperty(Schema.SimulationMetadata.roundsNum, this.getMaxNumberOfRounds());
            entity.setProperty(Schema.SimulationMetadata.beaconsNum, this.getBeaconsNum());
            entity.setProperty(Schema.SimulationMetadata.observersNum, this.getObserversNum());
            entity.setProperty(Schema.SimulationMetadata.rowsNum, this.getRowNum());
            entity.setProperty(Schema.SimulationMetadata.colsNum, this.getColNum());
            entity.setProperty(Schema.SimulationMetadata.beaconMovementStrategy, this.getBeaconMovementStrategy().toString());
            entity.setProperty(Schema.SimulationMetadata.observerMovementStrategy, this.getObserverMovementStrategy().toString());
            entity.setProperty(Schema.SimulationMetadata.observerAwakenessStrategy, this.getAwakenessStrategyType().toString());
            entity.setProperty(Schema.SimulationMetadata.radius, this.getRadius());
            entity.setProperty(Schema.SimulationMetadata.awakenessCycle, this.getAwakenessCycle());
            entity.setProperty(Schema.SimulationMetadata.awakenessDuration, this.getAwakenessDuration());
            entity.setProperty(Schema.SimulationMetadata.observersDensity, (double) this.getObserversNum() / (this.getRowNum() * this.getColNum()));
            entity.setProperty(Schema.SimulationMetadata.awakenessRatio, (double) this.getAwakenessDuration() / this.getAwakenessCycle());

            // Write to datastore and return key (as string).
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            return KeyFactory.keyToString(datastore.put(entity));
        }
    }
}