package com.google.research.bleth.services;

import com.google.appengine.api.datastore.*;

/**
 * DatabaseService is a singleton class wrapping the application's datastore instance.
 * DatabaseService provides a global point of access to any read/write operation using the datastore instance.
 */
public class DatabaseService {

    private static DatabaseService instance = null;
    private DatastoreService datastore;

    /**
     * A private constructor.
     * Ensures that the only way to create an instance of the class is by calling the getInstance() static method.
     */
    private DatabaseService() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    /**
     * Static method to create an instance of the DatabaseService class.
     * @return a DatabaseService class instance.
     */
    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    /**
     * Create an entity of baord state based on input params and write it to datastore.
     * @param simulationId is the Id of the simulation associated with the input board state.
     * @param round is the round number in the simulation that matches the input board state.
     * @param boardState is a JSON string represnting the state of the board.
     *                   Is parsable to a 3D array of strings (representing the agents' Ids).
     * @param isReal indicates the board type (true for real board, false for estimated board).
     * @throws PreparedQuery.TooManyResultsException if a board state with the same simulationId and round already exists.
     */
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

    /**
     * Retrieve the board state who matches the input params (if exists) and return it as a JSON string.
     * @param simulationId is the Id of the simulation.
     * @param round is the round number in the simulation.
     * @param isReal indicates the board type (true for real board, false for estimated board).
     * @return a JSON string representing the corresponding board state, or null if it doesn't exist.
     * @throws PreparedQuery.TooManyResultsException if there are more than a single entity matching the input params.
     */
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

        Entity boardStateEntity = pq.asSingleEntity();
        if (boardStateEntity == null) {
            return null;
        } else {
            return (String) boardStateEntity.getProperty("state");
        }
    }

    /**
     * Delete all real or estimated simulations associated with the input simulation Id.
     * @param simulationId is the Id of the simulation to delete all board states.
     * @param isReal indicates the board type (true for real board, false for estimated board).
     */
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
