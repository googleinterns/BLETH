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

    public void writeRealBoardState(String simulationId, int round, String boardState) {
        // Create new entity.
        Entity boardStateEntity = new Entity("TracingRealBoardState");
        boardStateEntity.setProperty("simulationId", simulationId);
        boardStateEntity.setProperty("round", round);
        boardStateEntity.setProperty("state", boardState);

        // Write to datastore.
        datastore.put(boardStateEntity);
    }

    public void writeEstimatedBoardState(String simulationId, int round, String boardState) {
        // Create new entity.
        Entity boardStateEntity = new Entity("TracingEstimatedBoardState");
        boardStateEntity.setProperty("simulationId", simulationId);
        boardStateEntity.setProperty("round", round);
        boardStateEntity.setProperty("state", boardState);

        // Write to datastore.
        datastore.put(boardStateEntity);
    }

    public String getRealBoardState(String simulationId, int round) throws PreparedQuery.TooManyResultsException {

        Query boardStateBySimulationAndRoundQuery = new Query("TracingRealBoardState").setFilter(
                Query.CompositeFilterOperator.and(
                        (new Query.FilterPredicate("simulationId", Query.FilterOperator.EQUAL, simulationId)),
                        (new Query.FilterPredicate("round", Query.FilterOperator.EQUAL, round))
                )
        );

        PreparedQuery pq = datastore.prepare(boardStateBySimulationAndRoundQuery);
        return (String) pq.asSingleEntity().getProperty("state");
    }

    public String getEstimatedBoardState(String simulationId, int round) throws PreparedQuery.TooManyResultsException {

        Query boardStateBySimulationAndRoundQuery = new Query("TracingEstimatedBoardState").setFilter(
                Query.CompositeFilterOperator.and(
                        (new Query.FilterPredicate("simulationId", Query.FilterOperator.EQUAL, simulationId)),
                        (new Query.FilterPredicate("round", Query.FilterOperator.EQUAL, round))
                )
        );

        PreparedQuery pq = datastore.prepare(boardStateBySimulationAndRoundQuery);
        return (String) pq.asSingleEntity().getProperty("state");
    }
}
