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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.research.bleth.simulator.Schema;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** A servlet used for displaying experiments. */
@WebServlet("/list-experiments")
public class ListExperimentsServlet extends HttpServlet {

    private static final DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static final Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImmutableMap.Builder<String, String> experiments = new ImmutableMap.Builder<>();
        Query experimentsQuery = new Query(Schema.Experiment.entityKind);
        PreparedQuery experimentsPreparedQuery = datastore.prepare(experimentsQuery);
        for (Entity entity : experimentsPreparedQuery.asIterable()) {
            experiments.put(KeyFactory.keyToString(entity.getKey()),
                    (String) entity.getProperty(Schema.Experiment.experimentTitle));
        }
        response.setContentType("application/json;");
        response.getWriter().println(gson.toJson(experiments.build()));
    }
}
