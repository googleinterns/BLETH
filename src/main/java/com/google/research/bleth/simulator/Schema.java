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

/** A class providing a single access point for datastore entities kinds and schemas. */
public class Schema {
    /** A class providing a single access point for the SimulationMetadata entity schema. */
    public static class SimulationMetadata {
        public static final String entityKind = "SimulationMetadata";
        public static final String description = "description";
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
        public static final String entityKindBeaconsObserved = "BeaconsObservedStats";
        public static final String entityKindBeaconsObservedIntervals = "BeaconsObservedIntervals";
        public static final String beaconId = "beaconId";
        public static final String observedPercent = "observedPercent";
        public static final String minimumLengthObservedInterval = "minimumLengthObservedInterval";
        public static final String minimumLengthUnobservedInterval = "minimumLengthUnobservedInterval";
        public static final String maximumLengthObservedInterval = "maximumLengthObservedInterval";
        public static final String maximumLengthUnobservedInterval = "maximumLengthUnobservedInterval";
        public static final String averageLengthObservedInterval = "averageLengthObservedInterval";
        public static final String averageLengthUnobservedInterval = "averageLengthUnobservedInterval";
        public static final String intervalStart = "intervalStart";
        public static final String intervalEnd = "intervalEnd";
        public static final String intervalObserved = "intervalObserved";
    }

    /** A class providing a single access point for the Experiment entity schema. */
    public static class Experiment {
        public static final String entityKind = "Experiment";
        public static final String experimentTitle = "experimentTitle";
        public static final String simulationsLeft = "simulationsLeft";
    }

    /** A class providing a single access point for the ExperimentsToSimulations entity schema. */
    public static class ExperimentsToSimulations {
        public static final String entityKind = "ExperimentsToSimulations";
        public static final String experimentId = "experimentId";
        public static final String simulationId = "simulationId";
    }
}
