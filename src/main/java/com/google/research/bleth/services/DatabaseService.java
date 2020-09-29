package com.google.research.bleth.services;

import com.google.appengine.api.datastore.*;

public class DatabaseService {

    private static DatabaseService instance = null;
    private DatastoreService datastore;

    private DatabaseService() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    public void writeBoardState(String simulationId, int round, String boardState, boolean isReal)
            throws PreparedQuery.TooManyResultsException {

        // Validate there is no such board state in datastore.
        if (this.getBoardState(simulationId, round, isReal) != null) {
            throw new PreparedQuery.TooManyResultsException();
        }

        // Create new entity.
        Entity boardStateEntity;
        if (isReal) {
            boardStateEntity = new Entity("TracingRealBoardState");
        } else {
            boardStateEntity = new Entity("TracingEstimatedBoardState");
        }

        // Set properties.
        boardStateEntity.setProperty("simulationId", simulationId);
        boardStateEntity.setProperty("round", round);
        boardStateEntity.setProperty("state", boardState);

        // Write to datastore.
        datastore.put(boardStateEntity);
    }

    public String getBoardState(String simulationId, int round, boolean isReal)
            throws PreparedQuery.TooManyResultsException {

        // Determine query kind.
        String queryKind;
        if (isReal) {
            queryKind = "TracingRealBoardState";
        } else {
            queryKind = "TracingEstimatedBoardState";
        }

        // Set simple predicates.
        Query.FilterPredicate p1 =
                new Query.FilterPredicate("simulationId", Query.FilterOperator.EQUAL, simulationId);
        Query.FilterPredicate p2 =
                new Query.FilterPredicate("round", Query.FilterOperator.EQUAL, round);

        // Compose simple predicates.
        Query.CompositeFilter composedFilter = Query.CompositeFilterOperator.and(p1, p2);

        // Create query and return result.
        Query boardStateBySimulationAndRoundQuery = new Query(queryKind).setFilter(composedFilter);
        PreparedQuery pq = datastore.prepare(boardStateBySimulationAndRoundQuery);
        return (String) pq.asSingleEntity().getProperty("state");
    }

    public void deleteAllSimulationBoardStates(String simulationId, boolean isReal) {

        // Determine query kind.
        String queryKind;
        if (isReal) {
            queryKind = "TracingRealBoardState";
        } else {
            queryKind = "TracingEstimatedBoardState";
        }

        // Build query and fetch results.
        Query.FilterPredicate predicate =
                new Query.FilterPredicate("simulationId", Query.FilterOperator.EQUAL, simulationId);

        Query deleteRealBoardStateQuery = new Query(queryKind).setFilter(predicate);
        PreparedQuery toDelete = datastore.prepare(deleteRealBoardStateQuery);

        // Delete entities.
        for (Entity entity : toDelete.asIterable()) {
            Key keyToDelete = entity.getKey();
            datastore.delete(keyToDelete);
        }
    }
}
