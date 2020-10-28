package com.google.research.bleth.simulator;

/** A class providing a single access point for datastore entities kinds and schemas. */
public class Schema {
    /** A class providing a single access point for the SimulationMetadata entity schema. */
    public static class SimulationMetadata {
        public static String entityKind = "SimulationMetadata";
        public static String type = "type";
        public static String roundsNum = "roundsNum";
        public static String beaconsNum = "beaconsNum";
        public static String observersNum = "observersNum";
        public static String rowsNum = "rowsNum";
        public static String colsNum = "colsNum";
        public static String beaconMovementStrategy = "beaconMovementStrategy";
        public static String observerMovementStrategy = "observerMovementStrategy";
        public static String observerAwakenessStrategy = "observerAwakenessStrategy";
        public static String radius = "radius";
        public static String awakenessCycle = "awakenessCycle";
        public static String awakenessDuration = "awakenessDuration";
        public static String observersDensity = "observersDensity";
        public static String awakenessRatio = "awakenessRatio";
    }

    /** A class providing a single access point for the RealBoardState / EstimatedBoardState entity schema. */
    public static class BoardState {
        public static String entityKindReal = "RealBoardState";
        public static String entityKindEstimated = "EstimatedBoardState";
        public static String simulationId = "simulationId";
        public static String round = "round";
        public static String agentId = "agentId";
        public static String rowNum = "rowNum";
        public static String colNum = "colNum";
    }
}
