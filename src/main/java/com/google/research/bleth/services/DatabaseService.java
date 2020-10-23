package com.google.research.bleth.services;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.Multimap;
import com.google.research.bleth.exceptions.BoardStateAlreadyExistsException;
import com.google.research.bleth.exceptions.ExceedingRoundException;
import com.google.research.bleth.simulator.AbstractSimulation;
import com.google.research.bleth.simulator.Board;
import com.google.research.bleth.simulator.IAgent;
import com.google.research.bleth.simulator.Location;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * DatabaseService is a singleton class wrapping the application's datastore instance.
 * DatabaseService provides a global point of access to any read/write operation using the datastore instance.
 *
 * DatabaseService provides API for:
 * - Writing simulation metadata by the builder responsible for the simulation construction (which returns the unique id
 * assigned to the simulation, encoded to a string).
 * - Writing a board state to the db.
 * - Reading a board state from the db (which returns null if provided round exceeds simulation's max number of round,
 * or a json string encoding the board state).
 */
public class DatabaseService {

    private static DatabaseService instance = null;
    private DatastoreService datastore;
    private Gson gson = new Gson();

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
     * Write an entity providing simulation metadata.
     * @param builder is the builder responsible for the simulation construction.
     * @return the new simulation id (encoded to string).
     */
    public String writeMetadata(AbstractSimulation.Builder builder) {
        // Create new entity.
        Entity entity = new Entity("Simulation");

        // Set properties.
        entity.setProperty("type", builder.getSimulationType());
        entity.setProperty("roundsNum", builder.getMaxNumberOfRounds());
        entity.setProperty("beaconsNum", builder.getBeaconsNum());
        entity.setProperty("observersNum", builder.getObserversNum());
        entity.setProperty("rowNum", builder.getRowNum());
        entity.setProperty("colNum", builder.getColNum());
        entity.setProperty("beaconMovementStrategy", builder.getBeaconMovementStrategy().toString());
        entity.setProperty("observerMovementStrategy", builder.getObserverMovementStrategy().toString());
        entity.setProperty("observerAwakenessStrategy", builder.getAwakenessStrategyType().toString());
        entity.setProperty("radius", builder.getRadius());
        entity.setProperty("awakenessCycle", builder.getAwakenessCycle());
        entity.setProperty("awakenessDuration", builder.getAwakenessDuration());
        entity.setProperty("observersDensity", (double) builder.getObserversNum() / (builder.getRowNum() * builder.getColNum()));
        entity.setProperty("awakenessRatio", (double) builder.getAwakenessDuration() / builder.getAwakenessCycle());

        // Write to datastore and return key (as string).
        return KeyFactory.keyToString(datastore.put(entity));
    }

    /**
     * Update RealBoard / EstimatedBoard by writing entities indicating the location of each agent on the given board,
     * attached to the simulationId and the round number.
     * @param simulationId is the simulation id associated with the state represented by the entities to be written.
     * @param round is the round number represented by the entities to be written.
     * @param board is the board to write its state to the db.
     */
    public void writeBoardState(String simulationId, int round, Board board) {
        String existingBoardState = readBoardState(simulationId, round, board.getType());
        String emptyJsonTable = gson.toJson(createEmptyTableWithDimensionsOf(simulationId));
        // If the same simulationId and round already recorded in the db, throw an exception.
        if (!existingBoardState.equals(emptyJsonTable)) {
            throw new BoardStateAlreadyExistsException(board.getType() + "State with simulationId "
                    + simulationId + " at round " + round + " already exists.");
        }

        Multimap<Location, IAgent> agentsOnBoard = board.agentsOnBoard();
        for (Location location : agentsOnBoard.keys()) {
            for (IAgent agent : agentsOnBoard.get(location)) {
                // Create new entity.
                Entity entity = new Entity(board.getType());

                // Set properties.
                entity.setProperty("simulationId", simulationId);
                entity.setProperty("round", round);
                entity.setProperty("agentId", agent.getId());
                entity.setProperty("agentType", agent.getType());
                entity.setProperty("rowNum", location.row);
                entity.setProperty("colNum", location.col);

                // Write to datastore.
                datastore.put(entity);
            }
        }
    }

    /**
     * Read from RealBoard and construct a Json string encoding the board state.
     * @param simulationId is the simulation id to be retrieved.
     * @param round is the round to be retrieved.
     * @return a Json string encoding the board state, uses for visualization,
     * or null if provided round exceeds simulation's maximum number of rounds.
     */
    public String readRealBoardState(String simulationId, int round) {
        return readBoardState(simulationId, round, "RealBoard");
    }

    /**
     * Read from EstimatedBoard and construct a Json string encoding the board state.
     * @param simulationId is the simulation id to be retrieved.
     * @param round is the round to be retrieved.
     * @return a Json string encoding the board state, uses for visualization,
     * or null if provided round exceeds simulation's maximum number of rounds.
     */
    public String readEstimatedBoardState(String simulationId, int round) {
        return readBoardState(simulationId, round, "EstimatedBoard");
    }

    private String readBoardState(String simulationId, int round, String entityKind) {
        // throw an exception if provided round exceeds simulation's maximum number of rounds.
        if (!isRoundExistsInSimulation(simulationId, round)) {
            throw new ExceedingRoundException("Provided round " + round +
                    " exceeds maximum number of rounds of simulation " + simulationId);
        }

        // Set simple predicates.
        Query.FilterPredicate filterBySimulationId =
                new Query.FilterPredicate("simulationId", Query.FilterOperator.EQUAL, simulationId);
        Query.FilterPredicate filterByRound =
                new Query.FilterPredicate("round", Query.FilterOperator.EQUAL, round);

        // Compose simple predicates.
        Query.CompositeFilter composedFilter = Query.CompositeFilterOperator.and(filterBySimulationId, filterByRound);

        // Create query and return result.
        Query boardStateBySimulationIdAndRoundQuery = new Query(entityKind).setFilter(composedFilter);
        PreparedQuery boardStateBySimulationIdAndRoundPreparedQuery = datastore.prepare(boardStateBySimulationIdAndRoundQuery);

        // Return encoded board state.
        return toJsonTable(boardStateBySimulationIdAndRoundPreparedQuery.asIterable(), simulationId);
    }

    private String toJsonTable(Iterable<Entity> boardStateEntities, String simulationId) {
        // Retrieve simulations' dimensions.
        Entity simulationEntity = retrieveSimulationEntity(simulationId);
        int rowNum = ((Long) simulationEntity.getProperty("rowNum")).intValue();
        int colNum = ((Long) simulationEntity.getProperty("colNum")).intValue();

        // Create an empty table representing the board state.
        ArrayTable<Integer, Integer, ArrayList<String>> boardState = createEmptyTable(rowNum, colNum);

        // Fill board state with agents' string representations according to query result.
        for (Entity entity : boardStateEntities) {
            int row = ((Long) entity.getProperty("rowNum")).intValue();
            int col = ((Long) entity.getProperty("colNum")).intValue();
            String type = (String) entity.getProperty("agentType");
            Long id = (Long) entity.getProperty("agentId");
            boardState.get(row, col).add(type + id);
        }

        return gson.toJson(boardState);
    }

    private ArrayTable<Integer, Integer, ArrayList<String>> createEmptyTable(int rowNum, int colNum) {
        ArrayTable<Integer, Integer, ArrayList<String>> table;
        table = ArrayTable.create(IntStream.range(0, rowNum).boxed().collect(Collectors.toList()),
                IntStream.range(0, colNum).boxed().collect(Collectors.toList()));

        for (int row = 0; row < rowNum; row++) {
            for (int col = 0; col < colNum; col++) {
                table.set(row, col, new ArrayList<>());
            }
        }

        return table;
    }

    private ArrayTable<Integer, Integer, ArrayList<String>> createEmptyTableWithDimensionsOf(String simulationId) {
        Entity simulationEntity = retrieveSimulationEntity(simulationId);
        int rowNum = ((Long) simulationEntity.getProperty("rowNum")).intValue();
        int colNum = ((Long) simulationEntity.getProperty("colNum")).intValue();
        return createEmptyTable(rowNum, colNum);
    }

    private Entity retrieveSimulationEntity(String simulationId) {
        Key simulationKey = KeyFactory.stringToKey(simulationId);
        Query.Filter filterBySimulationId =
                new Query.FilterPredicate(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.EQUAL, simulationKey);
        Query simulationIdQuery = new Query("Simulation").setFilter(filterBySimulationId);
        PreparedQuery simulationIdPreparedQuery = datastore.prepare(simulationIdQuery);
        return simulationIdPreparedQuery.asSingleEntity();
    }

    private boolean isRoundExistsInSimulation(String simulationId, int round) {
        Entity simulationEntity = retrieveSimulationEntity(simulationId);
        int maxSimulationRound = ((Long) simulationEntity.getProperty("roundsNum")).intValue();
        return round < maxSimulationRound;
    }

    private DatabaseService() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }
}