package com.google.research.bleth.utils;

import static com.google.common.truth.Truth.assertThat;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
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
    private static final String ENTITY_KIND = "entityKind";
    private static final String NON_EXISTING_PROPERTY = "nonExistingProperty";
    private static final String EXISTING_PROPERTY = "existingProperty";


    @Before
    public void setUp() {
        helper.setUp();
    }

    // Join Test Cases.

    @Test
    public void createThreeSimulations_shouldRetrieveThreeStatsEntities() {
        int simulationsNum = 3; // Number of simulations to create.
        int roundsNum = 5;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 1;
        int observersNum = 1;
        int awakenessCycle = 2;
        int awakenessDuration = 1;
        double transmissionRadius = 2.0;

        // Create simulations.
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

        // Retrieve stats of all simulations.
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

        // Set a simple predicate (filter).
        Query.FilterPredicate filterByRowsNum =
                new Query.FilterPredicate(Schema.SimulationMetadata.rowsNum, Query.FilterOperator.EQUAL, rowsNum);

        // Create simulations matching the filter's condition.
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

        // Create simulations with matching the filter's condition.
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

        // Create simulations matching the filter's condition.
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

        // Create another simulation which does not match the condition.
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

    @Test
    public void noSimulationsMatchingCondition_shouldRetrieveEmptyList() {
        int simulationsNum = 3; // Number of simulations to create.
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
                new Query.FilterPredicate(Schema.SimulationMetadata.rowsNum, Query.FilterOperator.EQUAL, rowsNum + 1);
        Query.FilterPredicate filterByColsNum =
                new Query.FilterPredicate(Schema.SimulationMetadata.colsNum, Query.FilterOperator.EQUAL, colsNum + 1);

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

        assertThat(stats).isEmpty();
    }

    @Test
    public void noSimulationsExists_shouldRetrieveEmptyList() {
        List<Entity> stats = Queries.Join(Schema.SimulationMetadata.entityKind, Schema.StatisticsState.entityKindDistance,
                Schema.StatisticsState.simulationId, Optional.empty());

        assertThat(stats).isEmpty();
    }

    @Test
    public void provideFilterOfNonExistingProperty_shouldRetrieveEmptyList() {
        int roundsNum = 5;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 1;
        int observersNum = 1;
        int awakenessCycle = 2;
        int awakenessDuration = 1;
        double transmissionRadius = 2.0;

        // Create and run a single simulation.
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

        // Set a simple filter by non existing property.
        Query.FilterPredicate nonExistingPropertyFilter =
                new Query.FilterPredicate(NON_EXISTING_PROPERTY, Query.FilterOperator.EQUAL, 0);

        // Retrieve stats of all simulations matching the filter's condition.
        List<Entity> stats = Queries.Join(Schema.SimulationMetadata.entityKind, Schema.StatisticsState.entityKindDistance,
                Schema.StatisticsState.simulationId, Optional.of(nonExistingPropertyFilter));

        assertThat(stats).isEmpty();
    }

    @Test
    public void provideNonExistingForeignKey_shouldRetrieveEmptyList() {
        int roundsNum = 5;
        int rowsNum = 2;
        int colsNum = 2;
        int beaconsNum = 1;
        int observersNum = 1;
        int awakenessCycle = 2;
        int awakenessDuration = 1;
        double transmissionRadius = 2.0;

        // Create and run a single simulation.
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

        // Retrieve stats of all simulations matching the filter's condition.
        List<Entity> stats = Queries.Join(Schema.SimulationMetadata.entityKind, Schema.StatisticsState.entityKindDistance,
                NON_EXISTING_PROPERTY, Optional.empty());

        assertThat(stats).isEmpty();
    }

    // Single-property Aggregation Test Cases.

    @Test
    public void writeThreeEntitiesWithProperty_shouldCalculateAverageOfThreeValues() {
        writeEntityWithProperty(ENTITY_KIND, EXISTING_PROPERTY, 1.5);
        writeEntityWithProperty(ENTITY_KIND, EXISTING_PROPERTY, 0.5);
        writeEntityWithProperty(ENTITY_KIND, EXISTING_PROPERTY, 4.0);
        List<Entity> entities = retrieveEntities(ENTITY_KIND);
        double expectedAverage = 2.0;

        double actualAverage = Queries.Average(entities, EXISTING_PROPERTY);

        assertThat(actualAverage).isEqualTo(expectedAverage);
    }

    @Test
    public void writeTwoEntitiesWithPropertyAndOneEntityWithoutProperty_shouldCalculateAverageOfTwoValues() {
        writeEntityWithProperty(ENTITY_KIND, EXISTING_PROPERTY, 1.5);
        writeEntityWithProperty(ENTITY_KIND, EXISTING_PROPERTY, 0.5);
        writeEntityWithoutProperty(ENTITY_KIND);
        List<Entity> entities = retrieveEntities(ENTITY_KIND);
        double expectedAverage = 1.0;

        double actualAverage = Queries.Average(entities, EXISTING_PROPERTY);

        assertThat(actualAverage).isEqualTo(expectedAverage);
    }

    @Test
    public void noEntitiesExists_shouldCalculateAverageAsNaN() {
        List<Entity> entities = retrieveEntities(ENTITY_KIND);
        double expectedAverage = Double.NaN;

        double actualAverage = Queries.Average(entities, EXISTING_PROPERTY);

        assertThat(actualAverage).isEqualTo(expectedAverage);
    }

    @Test
    public void writeSingleEntityWithoutProperty_shouldCalculateAverageAsNaN() {
        writeEntityWithoutProperty(ENTITY_KIND);
        List<Entity> entities = retrieveEntities(ENTITY_KIND);
        double expectedAverage = Double.NaN;

        double actualAverage = Queries.Average(entities, EXISTING_PROPERTY);

        assertThat(actualAverage).isEqualTo(expectedAverage);
    }

    @After
    public void tearDown() {
        helper.tearDown();
    }

    private void writeEntityWithProperty(String entityKind, String property, double value) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity(entityKind);
        entity.setProperty(property, value);
        datastore.put(entity);
    }

    private void writeEntityWithoutProperty(String entityKind) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Entity entity = new Entity(entityKind);
        datastore.put(entity);
    }

    private List<Entity> retrieveEntities(String entityKind) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Query entitiesQuery = new Query(entityKind);
        PreparedQuery entitiesPreparedQuery = datastore.prepare(entitiesQuery);
        return entitiesPreparedQuery.asList(FetchOptions.Builder.withDefaults());
    }
}
