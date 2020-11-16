package com.google.research.bleth.simulator;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.common.base.Objects;
import com.google.common.collect.ArrayTable;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.research.bleth.exceptions.BoardStateAlreadyExistsException;
import com.google.research.bleth.exceptions.ExceedingRoundException;

import java.util.ArrayList;

/**
 * Represents a static snapshot of a (real or estimated) board, associated with a simulation id and a round.
 * The BoardState storing the minimal data required for visualization -
 * which is a matrix of agents' type and id encoded as strings.
 * The BoardState provides methods for writing itself to the db,
 * json serialization and static methods for reading real / estimated board states.
 */
public class BoardState {
    private final int rows;
    private final int cols;
    private final ArrayTable<Integer, Integer, ArrayList<String>> matrix;
    private final String simulationId;
    private final int round;
    private final String entityKind;

    /**
     * Create new BoardState.
     * @param rows is the number of rows.
     * @param cols is the number of columns.
     * @param matrix is an array table of array lists of strings, where each string encodes the agent's type and id.
     * @param simulationId is the associated simulation id.
     * @param round is the associated round.
     * @param entityKind is a string indicating the entity kind to create when this BoardState is written to datastore.
     *                   can be either "RealBoardState" or "EstimatedBoardState".
     */
    BoardState(int rows, int cols, ArrayTable<Integer, Integer, ArrayList<String>> matrix,
               String simulationId, int round, String entityKind) {
        this.rows = rows;
        this.cols = cols;
        this.matrix = matrix;
        this.simulationId = simulationId;
        this.round = round;
        this.entityKind = entityKind;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardState that = (BoardState) o;
        return round == that.round &&
                Objects.equal(matrix, that.matrix) &&
                Objects.equal(simulationId, that.simulationId) &&
                Objects.equal(entityKind, that.entityKind);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(matrix, simulationId, round, entityKind);
    }

    /**
     * Create multiple datastore entities (each represents an agent in its location) associated with the board state,
     * and write them to the db.
     * @throws BoardStateAlreadyExistsException if a board state associated with the same simulation id, round and entity kind
     * already exists in the db.
     */
    public void write() throws BoardStateAlreadyExistsException {
        // Throw an exception if a board state associated with the same simulation id, round and entity kind
        // already exists in the db.
        BoardState readBoardState = read(simulationId, round, entityKind);
        if (!readBoardState.matrix.equals(BoardStateFactory.createEmptyTable(rows, cols))) {
            throw new BoardStateAlreadyExistsException(this.entityKind + " with simulationId "
                    + simulationId + " at round " + round + " already exists in db.");
        }

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                for (String agentId : matrix.get(row, col)) {
                    // Create new entity.
                    Entity entity = new Entity(this.entityKind);

                    // Set properties.
                    entity.setProperty(Schema.BoardState.simulationId, simulationId);
                    entity.setProperty(Schema.BoardState.round, round);
                    entity.setProperty(Schema.BoardState.agentId, agentId);
                    entity.setProperty(Schema.BoardState.rowNum, row);
                    entity.setProperty(Schema.BoardState.colNum, col);

                    // Write to datastore.
                    datastore.put(entity);
                }
            }
        }
    }

    /** Return a JSON string representing the board state. */
    public String toJson() {
        return new Gson().toJson(this.matrix);
    }

    /** Returns a map that maps to each populated location the representations (type and id) of the agents on this location. */
    public Multimap<Location, String> agentsRepresentationsOnStateBoard() {
        ImmutableListMultimap.Builder<Location, String> locationsToRepresentations =
                                                        new ImmutableListMultimap.Builder<Location, String>();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                locationsToRepresentations.putAll(Location.create(row, col), matrix.get(row, col));
            }
        }
        return locationsToRepresentations.build();
    }

    /**
     * Read a real BoardState from the db.
     * @param simulationId is the simulation id.
     * @param round is the round.
     * @return a board state constructed from the data read from db (if no entity exists in the db but the provided round
     * exists in the simulation - thr returned board state is empty).
     * @throws ExceedingRoundException if the provided round does not exist in the simulation corresponding
     * with the provided simulation id.
     */
    public static BoardState readReal(String simulationId, int round) throws ExceedingRoundException {
        return read(simulationId, round, Schema.BoardState.entityKindReal);
    }

    /**
     * Read an estimated BoardState from the db.
     * @param simulationId is the simulation id.
     * @param round is the round.
     * @return a board state constructed from the data read from db (if no entity exists in the db but the provided round
     * exists in the simulation - thr returned board state is empty).
     * @throws ExceedingRoundException if the provided round does not exist in the simulation corresponding
     * with the provided simulation id.
     */
    public static BoardState readEstimated(String simulationId, int round) throws ExceedingRoundException {
        return read(simulationId, round, Schema.BoardState.entityKindEstimated);
    }

    private static BoardState read(String simulationId, int round, String entityKind) throws ExceedingRoundException {
        // throw and exception if the provided round does not exist in the simulation.
        if (!SimulationMetadata.isRoundExistsInSimulation(simulationId, round)) {
            throw new ExceedingRoundException("Provided round " + round +
                    " exceeds maximum number of rounds of simulation " + simulationId);
        }

        // Retrieve simulation dimensions.
        SimulationMetadata simulationMetadata = SimulationMetadata.read(simulationId);
        int rowNum = simulationMetadata.rowsNum;
        int colNum = simulationMetadata.colsNum;

        // Retrieve entities to construct board state.
        PreparedQuery boardStateBySimulationIdAndRoundPreparedQuery = prepareBoardStateQuery(simulationId, round, entityKind);
        ArrayTable<Integer, Integer, ArrayList<String>> matrix = BoardStateFactory.createEmptyTable(rowNum, colNum);

        for (Entity entity : boardStateBySimulationIdAndRoundPreparedQuery.asIterable()) {
            int row = ((Long) entity.getProperty(Schema.BoardState.rowNum)).intValue();
            int col = ((Long) entity.getProperty(Schema.BoardState.colNum)).intValue();
            String agentId = (String) entity.getProperty(Schema.BoardState.agentId);
            matrix.get(row, col).add(agentId);
        }

        return new BoardState(rowNum, colNum, matrix, simulationId, round, entityKind);
    }

    private static PreparedQuery prepareBoardStateQuery(String simulationId, int round, String entityKind) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        // Set simple predicates.
        Query.FilterPredicate filterBySimulationId =
                new Query.FilterPredicate(Schema.BoardState.simulationId, Query.FilterOperator.EQUAL, simulationId);
        Query.FilterPredicate filterByRound =
                new Query.FilterPredicate(Schema.BoardState.round, Query.FilterOperator.EQUAL, round);

        // Compose simple predicates.
        Query.CompositeFilter composedFilter = Query.CompositeFilterOperator.and(filterBySimulationId, filterByRound);

        // Create query and return result.
        Query boardStateBySimulationIdAndRoundQuery = new Query(entityKind).setFilter(composedFilter);
        return datastore.prepare(boardStateBySimulationIdAndRoundQuery);
    }
}
