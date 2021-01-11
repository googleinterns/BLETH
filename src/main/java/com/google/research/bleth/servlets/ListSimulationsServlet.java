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

import com.google.appengine.api.datastore.Query;
import com.google.appengine.repackaged.com.google.common.base.Ascii;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.research.bleth.exceptions.MissingSortingParameterException;
import com.google.research.bleth.simulator.SimulationMetadata;
import com.google.research.bleth.utils.Queries;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for displaying simulations. */
@WebServlet("/list-simulations")
public class ListSimulationsServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Gson gson = new Gson(); // Used for json serialization.

        Optional<Queries.SortingParameters> sortingParameters = Optional.empty();
        if (request.getParameter("sortProperty") != null && request.getParameter("sortDirection") != null) {
            String sortProperty = request.getParameter("sortProperty");
            Query.SortDirection sortDirection = Query.SortDirection
                    .valueOf(Ascii.toUpperCase(request.getParameter("sortDirection")));
            sortingParameters = Optional.of(new Queries.SortingParameters(sortProperty, sortDirection));
        } else if (request.getParameter("sortProperty") == null ^ request.getParameter("sortDirection") == null) {
            String provided = request.getParameter("sortProperty") == null ? "sortDirection" : "sortProperty";
            String notProvided = provided.equals("sortProperty") ? "sortDirection" : "sortProperty";
            throw new MissingSortingParameterException(provided + " was provided, but " + notProvided + " wasn't.");
        }

        ImmutableMap.Builder<String, JsonElement> simulationsAsJson = new ImmutableMap.Builder<>();
        SimulationMetadata.listSimulations(sortingParameters)
                .forEach((id, metadata) -> simulationsAsJson.put(id, gson.toJsonTree(metadata)));

        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(simulationsAsJson.build()));
    }
}
