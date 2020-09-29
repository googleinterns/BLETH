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

    private void writeBoardState(String simulationId, int round, String boardState, String entityKind)
            throws PreparedQuery.TooManyResultsException {

        // Validate there is no such board state in datastore.
        if (this.getBoardState(simulationId, round, entityKind) != null) {
            throw new PreparedQuery.TooManyResultsException();
        }

        // Create new entity.
        Entity boardStateEntity = new Entity(entityKind);

        // Set properties.
        boardStateEntity.setProperty("simulationId", simulationId);
        boardStateEntity.setProperty("round", round);
        boardStateEntity.setProperty("state", boardState);

        // Write to datastore.
        datastore.put(boardStateEntity);
    }

    /**
     * Create an entity of a real baord state based on input params and write it to datastore.
     * @param simulationId is the Id of the simulation associated with the input board state.
     * @param round is the round number in the simulation that matches the input board state.
     * @param boardState is a JSON string represnting the state of the board.
     *                   Is parsable to a 3D array of strings (representing the agents' Ids).
     * @throws PreparedQuery.TooManyResultsException if a board state with the same simulationId and round already exists.
     */
    public void writeRealBoardState(String simulationId, int round, String boardState)
            throws PreparedQuery.TooManyResultsException {
        writeBoardState(simulationId, round, boardState, "TracingRealBoardState");
    }

    /**
     * Create an entity of an estimated baord state based on input params and write it to datastore.
     * @param simulationId is the Id of the simulation associated with the input board state.
     * @param round is the round number in the simulation that matches the input board state.
     * @param boardState is a JSON string represnting the state of the board.
     *                   Is parsable to a 3D array of strings (representing the agents' Ids).
     * @throws PreparedQuery.TooManyResultsException if a board state with the same simulationId and round already exists.
     */
    public void writeEstimatedBoardState(String simulationId, int round, String boardState)
            throws PreparedQuery.TooManyResultsException {
        writeBoardState(simulationId, round, boardState, "TracingEstimatedBoardState");
    }

    private String getBoardState(String simulationId, int round, String queryKind)
            throws PreparedQuery.TooManyResultsException {

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
     * Retrieve the real board state who matches the input params (if exists) and return it as a JSON string.
     * @param simulationId is the Id of the simulation.
     * @param round is the round number in the simulation.
     * @return a JSON string representing the corresponding board state, or null if it doesn't exist.
     * @throws PreparedQuery.TooManyResultsException if there are more than a single entity matching the input params.
     */
    public String getRealBoardState(String simulationId, int round)
            throws PreparedQuery.TooManyResultsException {
        return getBoardState(simulationId, round, "TracingRealBoardState");
    }

    /**
     * Retrieve the estimated board state who matches the input params (if exists) and return it as a JSON string.
     * @param simulationId is the Id of the simulation.
     * @param round is the round number in the simulation.
     * @return a JSON string representing the corresponding board state, or null if it doesn't exist.
     * @throws PreparedQuery.TooManyResultsException if there are more than a single entity matching the input params.
     */
    public String getEstimatedBoardState(String simulationId, int round)
            throws PreparedQuery.TooManyResultsException {
        return getBoardState(simulationId, round, "TracingEstimatedBoardState");
    }

    private void deleteAllSimulationBoardStates(String simulationId, String queryKind) {

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

    /**
     * Delete all real simulations associated with the input simulation Id.
     * @param simulationId is the Id of the simulation to delete all board states.
     */
    public void deleteAllSimulationRealBoardStates(String simulationId) {
        deleteAllSimulationBoardStates(simulationId, "TracingRealBoardState");
    }

    /**
     * Delete all estimated simulations associated with the input simulation Id.
     * @param simulationId is the Id of the simulation to delete all board states.
     */
    public void deleteAllSimulationEstimatedBoardStates(String simulationId) {
        deleteAllSimulationBoardStates(simulationId, "TracingEstimatedBoardState");
    }
}
