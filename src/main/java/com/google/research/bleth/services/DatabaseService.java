package com.google.research.bleth.services;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

public class DatabaseService {

    private static DatabaseService instance = null;
    private DatastoreService datastore;

    private DatabaseService() {
        datastore = DatastoreServiceFactory.getDatastoreService();
    }

    public static DatabaseService getInstance() {
        if (instance == null)
            instance = new DatabaseService();

        return instance;
    }

    public void writeRealBoardState(String simulationId, String round, String boardState) {
        //todo
    }

    public void writeEstimatedBoardState(String simulationId, String round, String boardState) {
        //todo
    }
}
