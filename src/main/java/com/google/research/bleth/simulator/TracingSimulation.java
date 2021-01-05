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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Random;

/**
 * A simulation in which the global resolver estimates beacons' real locations based on information received from observers.
 * Beacons transmit their unique static IDs each round.
 * Agents move according to a movement strategy.
 * Observers change their awakeness states according to an awakeness strategy.
 * Location estimation occurs each round and are based on both previous estimations and newly received information.
 */
public class TracingSimulation extends AbstractSimulation {

    public static class Builder extends AbstractSimulation.Builder {

        @Override
        public void validateArguments() {
            checkArgument(rowNum > 0 && colNum > 0,
                    "Board dimensions must be positive.");
            checkArgument(beaconsNum > 0 && observersNum > 0,
                    "Number of beacons and number of observers must be positive.");
            checkArgument(awakenessCycle > 0 && awakenessDuration > 0,
                    "Both awakeness cycle and duration must be positive.");
            checkArgument(awakenessCycle >= awakenessDuration,
                    "Awakeness cycle must be greater or equal to duration.");
            checkArgument(transmissionThresholdRadius > 0,
                    "Transmission threshold radius must be positive.");
            checkArgument(maxNumberOfRounds > 0,
                    "Maximum number of rounds must be positive.");
            checkNotNull(beaconMovementStrategyType, "No beacon movement strategy has been set.");
            checkNotNull(observerMovementStrategyType, "No observer movement strategy has been set.");
            checkNotNull(awakenessStrategyType, "No awakeness strategy type has been set.");
        }

        @Override
        void initializeObservers() {
            Random rand = new Random();
            ObserverFactory observerFactory = new ObserverFactory();
            MovementStrategyFactory movementStrategyFactory = new MovementStrategyFactory(observerMovementStrategyType);
            AwakenessStrategyFactory awakenessStrategyFactory = new AwakenessStrategyFactory(awakenessStrategyType);
            for (int i = 0; i < observersNum; i++) {
                Location initialLocation = Location.create(rand.nextInt(rowNum), rand.nextInt(colNum));
                IAwakenessStrategy awakenessStrategy = awakenessStrategyFactory.createStrategy(awakenessCycle, awakenessDuration);
                IMovementStrategy movementStrategy = movementStrategyFactory.createStrategy();
                Observer observer = observerFactory.createObserver(initialLocation, movementStrategy, resolver,
                realBoard, awakenessStrategy);
                observers.add(observer);
            }
        }

        @Override
        void initializeBeacons() {
            Random rand = new Random();
            BeaconFactory beaconFactory = new BeaconFactory();
            MovementStrategyFactory movementStrategyFactory = new MovementStrategyFactory(beaconMovementStrategyType);
            for (int i = 0; i < beaconsNum; i++) {
                Location initialLocation = Location.create(rand.nextInt(rowNum), rand.nextInt(colNum));
                IMovementStrategy movementStrategy = movementStrategyFactory.createStrategy();
                Beacon beacon = beaconFactory.createBeacon(initialLocation, movementStrategy, realBoard);
                beacons.add(beacon);
            }
        }

        @Override
        public AbstractSimulation build() {
            validateArguments();
            this.realBoard = new RealBoard(this.rowNum, this.colNum);
            initializeBeacons();
            this.resolver = GlobalResolver.create(this.rowNum, this.colNum, this.beacons);
            initializeObservers();
            writeMetadata();
            return new TracingSimulation(this);
        }
    }

    private TracingSimulation(Builder builder) {
        super(builder);
    }
}
