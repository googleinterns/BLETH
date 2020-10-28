package com.google.research.bleth.simulator;

/** A placeholder class for representing a BLETH simulation. */
public abstract class AbstractSimulation {

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

        /**
         * Write simulation metadata to the db.
         * @return the unique Id assigned to the datastore entity as a string.
         */
        String writeMetadata() {
            return new SimulationMetadata(this).write();
        }
    }
}