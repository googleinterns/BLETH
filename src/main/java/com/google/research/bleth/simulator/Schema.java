package com.google.research.bleth.simulator;

/** A class providing a single access point for datastore entities kinds and schemas. */
public class Schema {
    /** A class providing a single access point for the SimulationMetadata entity schema. */
    public static class SimulationMetadata {
        public static final String entityKind = "SimulationMetadata";
        public static final String description = "description";
        public static final String type = "type";
        public static final String roundsNum = "roundsNum";
        public static final String beaconsNum = "beaconsNum";
        public static final String observersNum = "observersNum";
        public static final String rowsNum = "rowsNum";
        public static final String colsNum = "colsNum";
        public static final String beaconMovementStrategy = "beaconMovementStrategy";
        public static final String observerMovementStrategy = "observerMovementStrategy";
        public static final String observerAwakenessStrategy = "observerAwakenessStrategy";
        public static final String transmissionThresholdRadius = "transmissionThresholdRadius";
        public static final String awakenessCycle = "awakenessCycle";
        public static final String awakenessDuration = "awakenessDuration";
        public static final String observersDensity = "observersDensity";
        public static final String awakenessRatio = "awakenessRatio";
    }

    /** A class providing a single access point for the RealBoardState / EstimatedBoardState entity schema. */
    public static class BoardState {
        public static final String entityKindReal = "RealBoardState";
        public static final String entityKindEstimated = "EstimatedBoardState";
        public static final String simulationId = "simulationId";
        public static final String round = "round";
        public static final String agentId = "agentId";
        public static final String rowNum = "rowNum";
        public static final String colNum = "colNum";
    }

    /** A class providing a single access point for the StatsState entity schema. */
    public static class StatisticsState {
        public static final String simulationId = "simulationId";
        public static final String entityKindDistance = "DistanceStats";
        public static final String entityKindBeaconsObservedPercent = "BeaconsObservedPercentStats";
    }
}
