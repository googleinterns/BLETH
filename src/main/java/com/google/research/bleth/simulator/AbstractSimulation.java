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

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableMap.toImmutableMap;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Table;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** An abstract class representing a BLETH simulation. */
public abstract class AbstractSimulation {

    private final String id;
    private int currentRound = 0;
    private final int maxNumberOfRounds;
    private final RealBoard board;
    protected final ImmutableList<Beacon> beacons;
    protected final ImmutableList<Observer> observers;
    private final IGlobalResolver resolver;
    private final double transmissionThresholdRadius;

    private final HashMap<String, Double> distancesStats = new HashMap<>();
    private final HashMap<Beacon, ObservedInterval.Builder> beaconsObservedCurrentInterval = new HashMap<>();
    private final LinkedListMultimap<Beacon, ObservedInterval> beaconsObservedIntervals = LinkedListMultimap.create();

    /** Returns a static snapshot of the real board at the current round. */
    BoardState getRealBoardState() {
        return BoardStateFactory.create(board, id,
                Math.min(currentRound, maxNumberOfRounds - 1)); // If the game is over return its final state
    }

    /** Returns a static snapshot of the resolver's estimated board at the current round. */
    BoardState getEstimatedBoardState() {
        return BoardStateFactory.create(resolver.getBoard(), id,
                Math.min(currentRound, maxNumberOfRounds - 1)); // If the game is over return its final state
    }

    /** Run entire simulation logic, including writing data to db. */
    public void run() {
        writeRoundState(); // round 0 is the initial simulation state
        currentRound++;
        while (currentRound < maxNumberOfRounds) {
            moveAgents();
            updateObserversAwaknessState();
            beaconsToObservers();
            observersToResolver();
            resolverEstimate();
            writeRoundState();
            updateDistanceSimulationStats();
            currentRound++;
        }
        closeBeaconsOpenObservedIntervals();
        writeSimulationStats();
    }

    /** Returns the simulation's Id. */
    public String getId() {
        return id;
    }

    /** Move all agents according to their movement strategies and update the real board. */
    void moveAgents() {
        beacons.forEach((AbstractAgent::move));
        observers.forEach((AbstractAgent::move));
    }

    /** Update all observers awakeness states according to their awakeness strategies. */
    void updateObserversAwaknessState() {
        observers.forEach((observer -> observer.updateAwakenessState(currentRound)));
    }

    /**
     * Pass transmissions from beacons to observers while taking into consideration world-physics parameters,
     * such as probability of transmission and distance between beacons and observers.
     */
    void beaconsToObservers() {
        for (Beacon beacon : beacons) {
            boolean observed = false;
            Transmission transmission = beacon.transmit();
            for (Observer observer : observers) {
                if (observer.isAwake()) {
                    double distance = distance(beacon.getLocation(), observer.getLocation());
                    if (distance <= transmissionThresholdRadius) {
                        observer.observe(transmission);
                        observed = true;
                    }
                }
            }
            updateBeaconObservedInterval(beacon, observed);
        }
    }

    /** Pass current-round information of transmission data from all observers to the simulation's resolver. */
    void observersToResolver() {
        observers.forEach((Observer::passInformationToResolver));
    }

    /** Update resolver's estimated board. */
    void resolverEstimate() {
        resolver.estimate();
    }

    /** Write current-round state of the simulation to db. */
    void writeRoundState() {
        BoardState realBoardState = BoardStateFactory.create(this.board, this.id, this.currentRound);
        BoardState estimatedBoardState = BoardStateFactory.create(this.resolver.getBoard(), this.id, this.currentRound);
        realBoardState.write();
        estimatedBoardState.write();
    }

    /** Gather statistical data of the current round and update the aggregated distance statistics based on all rounds. */
    void updateDistanceSimulationStats() {
        Map<Beacon, Location> beaconsToEstimatedLocations = resolver.getBeaconsToEstimatedLocations();
        List<Double> distances = beaconsToEstimatedLocations.keySet().stream() // ignore beacons that have never been observed
                .map(beacon -> distance(beaconsToEstimatedLocations.get(beacon), beacon.getLocation())).collect(toImmutableList());

        if (!distances.isEmpty()) { // distances is empty until the first round an observer observed a beacon
            double min = distances.stream().min(Double::compareTo).get();
            distancesStats.merge("min", min, Double::min);

            double max = distances.stream().max(Double::compareTo).get();
            distancesStats.merge("max", max, Double::max);

            double average = distances.stream().mapToDouble(Double::doubleValue).sum() / distances.size();
            double allRoundsAverage = (distancesStats.getOrDefault("avg", 0D) * (currentRound - 1) + average) / currentRound;
            distancesStats.put("avg", allRoundsAverage);
        }
    }

    /** Write final simulation statistical data to db. */
    void writeSimulationStats() {
        Map<String, List<Integer>> observedIntervals = beaconsObservedIntervals.keySet().stream()
                .collect(toImmutableMap(beacon -> String.valueOf(beacon.getId()),
                        beacon -> beaconsObservedIntervals.get(beacon).stream().filter(i -> i.observed())
                                .map(ObservedInterval::duration).collect(toImmutableList())));

        Map<String, List<Integer>> unobservedIntervals = beaconsObservedIntervals.keySet().stream()
                .collect(toImmutableMap(beacon -> String.valueOf(beacon.getId()),
                        beacon -> beaconsObservedIntervals.get(beacon).stream().filter(i -> !i.observed())
                                .map(ObservedInterval::duration).collect(toImmutableList())));

        Table<String, String, Double> observedStats = calculateObservedStats(observedIntervals, unobservedIntervals);

        StatisticsState statsState = StatisticsState.create(id, distancesStats, observedStats);
        statsState.writeDistancesStats();
        statsState.writeBeaconsObservedStats();
    }

    /** An abstract builder class designed to separate the construction of a simulation from its representation. */
    public static abstract class Builder {

        protected String id;
        protected String description;
        protected int maxNumberOfRounds;
        protected int rowNum;
        protected int colNum;
        protected RealBoard realBoard;
        protected IGlobalResolver resolver;
        protected int beaconsNum;
        protected int observersNum;
        protected List<Beacon> beacons = new ArrayList<>();
        protected List<Observer> observers = new ArrayList<>();
        protected MovementStrategyFactory.Type beaconMovementStrategyType;
        protected MovementStrategyFactory.Type observerMovementStrategyType;
        protected AwakenessStrategyFactory.Type awakenessStrategyType;
        protected double transmissionThresholdRadius;
        protected int awakenessCycle;
        protected int awakenessDuration;

        /** Return a string that describe the simulation. */
        public String getDescription() {
            return description;
        }

        /** Return the maximal number of rounds of the simulation created by the builder. */
        public int getMaxNumberOfRounds() {
            return maxNumberOfRounds;
        }

        /** Return the number of rows of the simulation created by the builder. */
        public int getRowNum() {
            return rowNum;
        }

        /** Return the number of columns of the simulation created by the builder. */
        public int getColNum() {
            return colNum;
        }

        /** Return the number of beacons of the simulation created by the builder. */
        public int getBeaconsNum() {
            return beaconsNum;
        }

        /** Return the number of observers of the simulation created by the builder. */
        public int getObserversNum() {
            return observersNum;
        }

        /** Return the beacons' movement strategy of the simulation created by the builder. */
        public MovementStrategyFactory.Type getBeaconMovementStrategyType() {
            return beaconMovementStrategyType;
        }

        /** Return the observers' movement strategy of the simulation created by the builder. */
        public MovementStrategyFactory.Type getObserverMovementStrategyType() {
            return observerMovementStrategyType;
        }

        /** Return the observers' awakeness strategy type of the simulation created by the builder. */
        public AwakenessStrategyFactory.Type getAwakenessStrategyType() {
            return awakenessStrategyType;
        }

        /** Return the threshold transmission radius of the simulation created by the builder. */
        public double getTransmissionThresholdRadius() {
            return transmissionThresholdRadius;
        }

        /** Return the awakeness cycle of the simulation created by the builder. */
        public int getAwakenessCycle() {
            return awakenessCycle;
        }

        /** Return the awakeness duration of the simulation created by the builder. */
        public int getAwakenessDuration() {
            return awakenessDuration;
        }

        /**
         * Set a description of the simulation.
         * @param description is the description of the simulation.
         * @return this, to provide chaining.
         */
        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        /**
         * Set number of rows in simulation.
         * @param rowNum is the number of rows in simulation.
         * @return this, to provide chaining.
         */
        public Builder setRowNum(int rowNum) {
            this.rowNum = rowNum;
            return this;
        }

        /**
         * Set number of columns in simulation.
         * @param colNum is the number of columns in simulation.
         * @return this, to provide chaining.
         */
        public Builder setColNum(int colNum) {
            this.colNum = colNum;
            return this;
        }

        /**
         * Set number of beacons in simulation.
         * @param beaconsNum is the number of beacons in simulation.
         * @return this, to provide chaining.
         */
        public Builder setBeaconsNum(int beaconsNum) {
            this.beaconsNum = beaconsNum;
            return this;
        }

        /**
         * Set number of observers in simulation.
         * @param observersNum is the number of observers in simulation.
         * @return this, to provide chaining.
         */
        public Builder setObserversNum(int observersNum) {
            this.observersNum = observersNum;
            return this;
        }

        /**
         * Set maximum number of rounds in simulation as the index of the last simulation round.
         * @param maxNumberOfRounds is the maximum number of rounds in simulation (index of last round).
         * @return this, to provide chaining.
         */
        public Builder setMaxNumberOfRounds(int maxNumberOfRounds) {
            this.maxNumberOfRounds = maxNumberOfRounds;
            return this;
        }

        /**
         * Set the threshold transmission radius.
         * @param transmissionThresholdRadius is the threshold transmission radius
         * @return this, to provide chaining.
         */
        public Builder setTransmissionThresholdRadius(double transmissionThresholdRadius) {
            this.transmissionThresholdRadius = transmissionThresholdRadius;
            return this;
        }

        /**
         * Set the awakeness cycle, which is the number of rounds in which every observer must have
         * an awakeness period.
         * @param awakenessCycle is the number of rounds to be considered as the cycle.
         * @return this, to provide chaining.
         */
        public Builder setAwakenessCycle(int awakenessCycle) {
            this.awakenessCycle = awakenessCycle;
            return this;
        }

        /**
         * Set the awakeness duration, which is the number of rounds in which an observer is awake in a
         * single awakeness cycle.
         * @param awakenessDuration is the number of rounds to be considered as the duration.
         * @return this, to provide chaining.
         */
        public Builder setAwakenessDuration(int awakenessDuration) {
            this.awakenessDuration = awakenessDuration;
            return this;
        }

        /**
         * Set the observers' awakeness strategy type, used for generating awakeness strategies for all observers.
         * @param awakenessStrategyType is the awakeness strategy type for all observers.
         * @return this, to provide chaining.
         */
        public Builder setAwakenessStrategyType(AwakenessStrategyFactory.Type awakenessStrategyType) {
            this.awakenessStrategyType = awakenessStrategyType;
            return this;
        }

        /**
         * Set the beacons' movement strategy type.
         * @param beaconMovementStrategyType is the movement strategy type for all beacons.
         * @return this, to provide chaining.
         */
        public Builder setBeaconMovementStrategyType(MovementStrategyFactory.Type beaconMovementStrategyType) {
            this.beaconMovementStrategyType = beaconMovementStrategyType;
            return this;
        }

        /**
         * Set the observers' movement strategy type.
         * @param observerMovementStrategyType is the movement strategy type for all observers.
         * @return this, to provide chaining.
         */
        public Builder setObserverMovementStrategyType(MovementStrategyFactory.Type observerMovementStrategyType) {
            this.observerMovementStrategyType = observerMovementStrategyType;
            return this;
        }

        /**
         * Write simulation metadata to the db.
         * @return the unique Id assigned to the datastore entity as a string.
         */
        String writeMetadata() {
            this.id = new SimulationMetadata(this).write();
            return this.id;
        }

        /** Validate all simulation builder arguments are legal, when a simulation is constructed using {@code buildNew()}. */
        abstract void validateArguments();

        /**
         * Create and initialize simulation observers in random initial locations using a factory, and store them in observers container.
         * Initializes the observers movement strategy according to the strategy passed to the builder.
         * Initializes the observers awakeness strategy according to the strategy type, awakeness cycle and awakeness duration
         * passed to the builder.
         */
        abstract void initializeObservers();

        /**
         * Create and initialize simulation beacons in random initial locations using a factory, and store them in observers container.
         * Create simple beacons for a tracing simulation and swapping beacons for a stalking simulation.
         * Initializes the beacons movement strategy according to the strategy passed to the builder.
         */
        abstract void initializeBeacons();

        /**
         * Construct a new simulation object.
         * @return a new Simulation object constructed with the builder parameters.
         */
        public abstract AbstractSimulation build();
    }

    // A protected constructor used by the concrete simulation classes' constructors.
    protected AbstractSimulation (Builder builder) {
        this.id = builder.id;
        this.maxNumberOfRounds = builder.maxNumberOfRounds;
        this.board = builder.realBoard;
        this.resolver = builder.resolver;
        this.transmissionThresholdRadius = builder.transmissionThresholdRadius;
        this.beacons = ImmutableList.copyOf(builder.beacons);
        this.observers = ImmutableList.copyOf(builder.observers);
    }

    private void updateBeaconObservedInterval(Beacon beacon, boolean observed) {
        ObservedInterval.Builder currentInterval = beaconsObservedCurrentInterval.get(beacon);
        if (currentInterval != null && currentInterval.observed() != observed) {
            beaconsObservedIntervals.put(beacon, currentInterval.build());
            beaconsObservedCurrentInterval.remove(beacon);
        }

        if (!beaconsObservedCurrentInterval.containsKey(beacon)) {
            beaconsObservedCurrentInterval.put(beacon, new AutoValue_ObservedInterval.Builder()
                                                                                  .setStart(currentRound)
                                                                                  .setObserved(observed));
        }
        beaconsObservedCurrentInterval.get(beacon).setEnd(currentRound);
    }

    private void closeBeaconsOpenObservedIntervals() {
        for (Beacon beacon : beaconsObservedCurrentInterval.keySet()) {
            ObservedInterval.Builder currentInterval = beaconsObservedCurrentInterval.get(beacon);
            beaconsObservedIntervals.put(beacon, currentInterval.build());
        }
        beaconsObservedCurrentInterval.clear();
    }

    private static double distance(Location firstLocation, Location secondLocation) {
        return Math.abs(firstLocation.row() - secondLocation.row()) + Math.abs(firstLocation.col() - secondLocation.col());
    }

    private Table<String, String, Double> calculateObservedStats(Map<String, List<Integer>> observedIntervals,
                                                                 Map<String, List<Integer>> unobservedIntervals) {
        ImmutableTable.Builder<String, String, Double> observedStats = new ImmutableTable.Builder<>();
        mapIdsToPercentOfSimulation(observedIntervals).forEach((beaconId, value) ->
                observedStats.put(beaconId, Schema.StatisticsState.observedPercent, value));
        mapIdsToMinValue(observedIntervals).forEach((beaconId, value) ->
                observedStats.put(beaconId, Schema.StatisticsState.minimumLengthObservedInterval, value));
        mapIdsToMinValue(unobservedIntervals).forEach((beaconId, value) ->
                observedStats.put(beaconId, Schema.StatisticsState.minimumLengthUnobservedInterval, value));
        mapIdsToMaxValue(observedIntervals).forEach((beaconId, value) ->
                observedStats.put(beaconId, Schema.StatisticsState.maximumLengthObservedInterval, value));
        mapIdsToMaxValue(unobservedIntervals).forEach((beaconId, value) ->
                observedStats.put(beaconId, Schema.StatisticsState.maximumLengthUnobservedInterval, value));
        mapIdsToAvgValue(observedIntervals).forEach((beaconId, value) ->
                observedStats.put(beaconId, Schema.StatisticsState.averageLengthObservedInterval, value));
        mapIdsToAvgValue(unobservedIntervals).forEach((beaconId, value) ->
                observedStats.put(beaconId, Schema.StatisticsState.averageLengthUnobservedInterval, value));
        return observedStats.build();
    }

    private Map<String, Double> mapIdsToPercentOfSimulation(Map<String, List<Integer>> idsToIntervals) {
        return idsToIntervals.entrySet().stream().collect(toImmutableMap(e -> e.getKey(),
                e -> e.getValue().stream().mapToDouble(Double::valueOf).sum() / (currentRound - 1)));
    }

    private Map<String, Double> mapIdsToMinValue(Map<String, List<Integer>> idsToIntervals) {
        return idsToIntervals.entrySet().stream().collect(toImmutableMap(e -> e.getKey(),
                e -> e.getValue().stream().mapToDouble(Double::valueOf).min().orElse(Double.NaN)));
    }

    private Map<String, Double> mapIdsToMaxValue(Map<String, List<Integer>> idsToIntervals) {
        return idsToIntervals.entrySet().stream().collect(toImmutableMap(e -> e.getKey(),
                e -> e.getValue().stream().mapToDouble(Double::valueOf).max().orElse(Double.NaN)));
    }

    private Map<String, Double> mapIdsToAvgValue(Map<String, List<Integer>> idsToIntervals) {
        return idsToIntervals.entrySet().stream().collect(toImmutableMap(e -> e.getKey(),
                e -> e.getValue().stream().mapToDouble(Double::valueOf).average().orElse(Double.NaN)));
    }
}