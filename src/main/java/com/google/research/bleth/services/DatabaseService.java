package com.google.research.bleth.services;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.research.bleth.simulator.Board;

public class DatabaseService {

    private static DatabaseService instance = null;
    private DatastoreService datastore;

    private DatabaseService() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public static DatabaseService getInstance() {
        if (instance == null)
            instance = new DatabaseService();

        return instance;
    }

    public void writeRealBoardState(String simulationId, String round, String boardState) {
        // Create new entity.
        Entity boardStateEntity = new Entity("TracingRealBoardState");
        boardStateEntity.setProperty("simulationId", simulationId);
        boardStateEntity.setProperty("round", round);
        boardStateEntity.setProperty("state", boardState);

        // Write to datastore.
        datastore.put(boardStateEntity);
    }

    public void writeEstimatedBoardState(String simulationId, String round, String boardState) {
        // Create new entity.
        Entity boardStateEntity = new Entity("TracingEstimatedBoardState");
        boardStateEntity.setProperty("simulationId", simulationId);
        boardStateEntity.setProperty("round", round);
        boardStateEntity.setProperty("state", boardState);

        // Write to datastore.
        datastore.put(boardStateEntity);
    }

    public String getRealBoardState(String simulationId, String round) {

        Query boardStateBySimulationAndRoundQuery = new Query("TracingRealBoardState")
                .setFilter(new Query.FilterPredicate("simulationId", Query.FilterOperator.EQUAL, simulationId))
                .setFilter(new Query.FilterPredicate("round", Query.FilterOperator.EQUAL, round));

        PreparedQuery pq = datastore.prepare(boardStateBySimulationAndRoundQuery);
        Entity boardStateEntity = pq.asSingleEntity();

        if (boardStateEntity != null) {
            return (String) boardStateEntity.getProperty("state");
        }

        // todo: change this to exception throwing
        else return new Board(5, 5).getState();
    }
}
