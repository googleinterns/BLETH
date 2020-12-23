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

package com.google.research.bleth.servlets;

import com.google.common.collect.Table;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.research.bleth.simulator.StatisticsState;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for reading simulations' statistical data of observed intervals. */
@WebServlet("/read-observed-stats")
public class ReadObservedStatisticsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new GsonBuilder().serializeNulls().serializeSpecialFloatingPointValues().create();

        String simulationId = request.getParameter("simulationId");
        Table<String, String, Double> observedStatistics = StatisticsState.readBeaconsObservedStats(simulationId);

        // Write to response.
        response.setContentType("application/json");
        response.getWriter().println(gson.toJson(observedStatistics));
    }
}
