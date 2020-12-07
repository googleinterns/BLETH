// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.research.bleth.simulator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Multimap;
import com.google.research.bleth.exceptions.ExceedingRoundException;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** A factory used for creating a BoardState from a given Board, simulationId and round. */
public class BoardStateFactory {
    /**
     * Create a new board state.
     * @param board is the board to be represented by the created board state.
     * @param simulationId is the simulation id associated with the board state.
     * @param round is the round associated with the board state.
     * @return a board state.
     */
    public static BoardState create(AbstractBoard board, String simulationId, int round) {
        checkArgument(round >= 0);
        checkNotNull(board);
        String entityKind = determineBoardStateEntityKind(board);
        if (!SimulationMetadata.isRoundExistsInSimulation(simulationId, round)) {
            throw new ExceedingRoundException("Provided round " + round +
                    " exceeds maximum number of rounds of simulation " + simulationId);
        }
        return new BoardState(board.getRowNum(), board.getColNum(),
                              toArrayTable(board), simulationId, round, entityKind);
    }

    /**
     * Create an empty array table of array lists of strings.
     * @param rowNum is the number of rows.
     * @param colNum is the number of columns.
     * @return an empty array table of array lists of strings.
     */
    static ArrayTable<Integer, Integer, ArrayList<String>> createEmptyTable(int rowNum, int colNum) {
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

    private static ArrayTable<Integer, Integer, ArrayList<String>> toArrayTable(AbstractBoard board) {
        int rowNum = board.getRowNum();
        int colNum = board.getColNum();
        ArrayTable<Integer, Integer, ArrayList<String>> boardStateTable = createEmptyTable(rowNum, colNum);
        Multimap<Location, IAgent> agentsOnBoard = board.agentsOnBoard();
        for (Location location : agentsOnBoard.keySet()) {
            for (IAgent agent : agentsOnBoard.get(location)) {
                boardStateTable.get(location.row(), location.col()).add(agent.getType() + agent.getId());
            }
        }

        return boardStateTable;
    }

    private static String determineBoardStateEntityKind(AbstractBoard board) {
        if (board instanceof RealBoard) { return Schema.BoardState.entityKindReal; }
        return Schema.BoardState.entityKindEstimated;
    }
}
