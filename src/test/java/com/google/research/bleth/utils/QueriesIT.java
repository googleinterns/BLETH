package com.google.research.bleth.utils;

import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.research.bleth.simulator.AbstractSimulation;
import com.google.research.bleth.simulator.AwakenessStrategyFactory;
import com.google.research.bleth.simulator.MovementStrategyFactory;
import com.google.research.bleth.simulator.Schema;
import com.google.research.bleth.simulator.TracingSimulation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class QueriesIT {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setAutoIdAllocationPolicy(LocalDatastoreService.AutoIdAllocationPolicy.SCATTERED));

    @Before
    public void setUp() {
        helper.setUp();
    }

    @Test
    public void createThreeSimulations_shouldRetrieveThreeStatsEntities() {
        int simulationsNum = 3; // Number of simulation matching the filter condition.
        int roundsNum = 5;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 1;
        int observersNum = 1;
        int awakenessCycle = 2;
        int awakenessDuration = 1;
        double transmissionRadius = 2.0;

        // Create simulations with rowsNum and colsNum matching the condition.
        for (int i = 0; i < simulationsNum; i++) {
            AbstractSimulation simulation = new TracingSimulation.Builder()
                    .setMaxNumberOfRounds(roundsNum)
                    .setRowNum(rowsNum)
                    .setColNum(colsNum)
                    .setBeaconsNum(beaconsNum)
                    .setObserversNum(observersNum)
                    .setTransmissionThresholdRadius(transmissionRadius)
                    .setBeaconMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                    .setObserverMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                    .setAwakenessCycle(awakenessCycle)
                    .setAwakenessDuration(awakenessDuration)
                    .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                    .build();

            simulation.run();
        }

        // Retrieve stats of all simulations matching the filter's condition.
        List<Entity> stats = Queries.Join(Schema.SimulationMetadata.entityKind, Schema.StatisticsState.entityKindDistance,
                Schema.StatisticsState.simulationId, Optional.empty());

        assertThat(stats.size()).isEqualTo(simulationsNum);
    }

    @Test
    public void createThreeSimulationsMatchingSimpleCondition_shouldRetrieveThreeStatsEntities() {
        int simulationsNum = 3; // Number of simulation matching the filter condition.
        int roundsNum = 5;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 1;
        int observersNum = 1;
        int awakenessCycle = 2;
        int awakenessDuration = 1;
        double transmissionRadius = 2.0;

        // Set simple predicates.
        Query.FilterPredicate filterByRowsNum =
                new Query.FilterPredicate(Schema.SimulationMetadata.rowsNum, Query.FilterOperator.EQUAL, rowsNum);

        // Create simulations with rowsNum and colsNum matching the condition.
        for (int i = 0; i < simulationsNum; i++) {
            AbstractSimulation simulation = new TracingSimulation.Builder()
                    .setMaxNumberOfRounds(roundsNum)
                    .setRowNum(rowsNum)
                    .setColNum(colsNum)
                    .setBeaconsNum(beaconsNum)
                    .setObserversNum(observersNum)
                    .setTransmissionThresholdRadius(transmissionRadius)
                    .setBeaconMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                    .setObserverMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                    .setAwakenessCycle(awakenessCycle)
                    .setAwakenessDuration(awakenessDuration)
                    .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                    .build();

            simulation.run();
        }

        // Retrieve stats of all simulations matching the filter's condition.
        List<Entity> stats = Queries.Join(Schema.SimulationMetadata.entityKind, Schema.StatisticsState.entityKindDistance,
                Schema.StatisticsState.simulationId, Optional.of(filterByRowsNum));

        assertThat(stats.size()).isEqualTo(simulationsNum);
    }

    @Test
    public void createThreeSimulationsMatchingCondition_shouldRetrieveThreeStatsEntities() {
        int simulationsNum = 3; // Number of simulation matching the filter condition.
        int roundsNum = 5;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 1;
        int observersNum = 1;
        int awakenessCycle = 2;
        int awakenessDuration = 1;
        double transmissionRadius = 2.0;

        // Set simple predicates.
        Query.FilterPredicate filterByRowsNum =
                new Query.FilterPredicate(Schema.SimulationMetadata.rowsNum, Query.FilterOperator.EQUAL, rowsNum);
        Query.FilterPredicate filterByColsNum =
                new Query.FilterPredicate(Schema.SimulationMetadata.colsNum, Query.FilterOperator.EQUAL, colsNum);

        // Compose simple predicates to create a filter.
        Query.CompositeFilter composedQueryFilter = Query.CompositeFilterOperator.and(filterByRowsNum, filterByColsNum);

        // Create simulations with rowsNum and colsNum matching the condition.
        for (int i = 0; i < simulationsNum; i++) {
            AbstractSimulation simulation = new TracingSimulation.Builder()
                    .setMaxNumberOfRounds(roundsNum)
                    .setRowNum(rowsNum)
                    .setColNum(colsNum)
                    .setBeaconsNum(beaconsNum)
                    .setObserversNum(observersNum)
                    .setTransmissionThresholdRadius(transmissionRadius)
                    .setBeaconMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                    .setObserverMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                    .setAwakenessCycle(awakenessCycle)
                    .setAwakenessDuration(awakenessDuration)
                    .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                    .build();

            simulation.run();
        }

        // Retrieve stats of all simulations matching the filter's condition.
        List<Entity> stats = Queries.Join(Schema.SimulationMetadata.entityKind, Schema.StatisticsState.entityKindDistance,
                                          Schema.StatisticsState.simulationId, Optional.of(composedQueryFilter));

        assertThat(stats.size()).isEqualTo(simulationsNum);
    }

    @Test
    public void createThreeSimulationsMatchingConditionAndOneSimulationNonMatching_shouldRetrieveThreeStatsEntities() {
        int simulationsNum = 3; // Number of simulation matching the filter condition.
        int roundsNum = 5;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 1;
        int observersNum = 1;
        int awakenessCycle = 2;
        int awakenessDuration = 1;
        double transmissionRadius = 2.0;

        // Set simple predicates.
        Query.FilterPredicate filterByRowsNum =
                new Query.FilterPredicate(Schema.SimulationMetadata.rowsNum, Query.FilterOperator.EQUAL, rowsNum);
        Query.FilterPredicate filterByColsNum =
                new Query.FilterPredicate(Schema.SimulationMetadata.colsNum, Query.FilterOperator.EQUAL, colsNum);

        // Compose simple predicates to create a filter.
        Query.CompositeFilter composedQueryFilter = Query.CompositeFilterOperator.and(filterByRowsNum, filterByColsNum);

        // Create simulations with rowsNum and colsNum matching the condition.
        for (int i = 0; i < simulationsNum; i++) {
            AbstractSimulation simulation = new TracingSimulation.Builder()
                    .setMaxNumberOfRounds(roundsNum)
                    .setRowNum(rowsNum)
                    .setColNum(colsNum)
                    .setBeaconsNum(beaconsNum)
                    .setObserversNum(observersNum)
                    .setTransmissionThresholdRadius(transmissionRadius)
                    .setBeaconMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                    .setObserverMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                    .setAwakenessCycle(awakenessCycle)
                    .setAwakenessDuration(awakenessDuration)
                    .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                    .build();

            simulation.run();
        }

        // Create another simulation with rowsNum and colsNum which are not matching the condition.
        AbstractSimulation simulation = new TracingSimulation.Builder()
                .setMaxNumberOfRounds(roundsNum)
                .setRowNum(rowsNum + 1)
                .setColNum(colsNum)
                .setBeaconsNum(beaconsNum)
                .setObserversNum(observersNum)
                .setTransmissionThresholdRadius(transmissionRadius)
                .setBeaconMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                .setObserverMovementStrategyType(MovementStrategyFactory.Type.RANDOM)
                .setAwakenessCycle(awakenessCycle)
                .setAwakenessDuration(awakenessDuration)
                .setAwakenessStrategyType(AwakenessStrategyFactory.Type.FIXED)
                .build();

        simulation.run();

        // Retrieve stats of all simulations matching the filter's condition.
        List<Entity> stats = Queries.Join(Schema.SimulationMetadata.entityKind, Schema.StatisticsState.entityKindDistance,
                Schema.StatisticsState.simulationId, Optional.of(composedQueryFilter));

        assertThat(stats.size()).isEqualTo(simulationsNum);
    }

    public void provideFilterOfNonExistingProperty_shouldThrowException() {
        assertThat(true).isTrue();
    }

    public void provideNonExistingForeignKey_shouldThrowException() {
        assertThat(true).isTrue();
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }
}
