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

    // todo: verify a board state with same sim id and round does not exists. if so - raise exception!
    public void writeBoardState(String simulationId, int round, String boardState, boolean isReal) {

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
}
