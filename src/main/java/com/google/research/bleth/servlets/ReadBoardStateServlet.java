// Copyright 2021 Google LLC
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

package com.google.research.bleth.servlets;

import com.google.research.bleth.simulator.BoardState;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for reading a board state from the db and write it to response as a JSON 2D array. */
@WebServlet("/read-board-state")
public class ReadBoardStateServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get board state parameters.
        int round = Integer.parseInt(request.getParameter("round"));
        String simulationId = request.getParameter("simulationId");
        boolean isReal = Boolean.parseBoolean(request.getParameter("isReal"));

        // Read board state.
        BoardState boardState;
        if (isReal) {
            boardState = BoardState.readReal(simulationId, round);
        } else {
            boardState = BoardState.readEstimated(simulationId, round);
        }

        // Write to response.
        response.setContentType("application/json;");
        response.getWriter().println(boardState.toJson());
    }
}
