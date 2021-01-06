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
