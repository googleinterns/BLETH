package com.google.research.bleth.utils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class Queries {
    public static List<Entity> Join(String primaryEntityKind, String secondaryEntityKind,
                                    String foreignKey, Optional<Query.Filter> primaryEntityFilter) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> result = new ArrayList<>();

        // Retrieve all primaryEntity ordered by their key, filtered by primaryEntityFilter (if provided).
        Query primaryMatchingFilterCondition = new Query(primaryEntityKind);
        primaryEntityFilter.ifPresent(primaryMatchingFilterCondition::setFilter);
        Iterator<String> primaryKeyIterator = datastore.prepare(primaryMatchingFilterCondition)
                .asList(FetchOptions.Builder.withDefaults())
                .stream()
                .map(entity -> KeyFactory.keyToString(entity.getKey()))
                .sorted()
                .iterator();

        // Retrieve all secondaryEntity ordered by their foreignKey.
        Query secondary = new Query(secondaryEntityKind)
                .addSort(foreignKey, Query.SortDirection.ASCENDING);
        Iterator<Entity> secondaryEntityIterator = datastore.prepare(secondary).asIterator();

        // If one of these queries is empty, return an empty list.
        if (!(primaryKeyIterator.hasNext() && secondaryEntityIterator.hasNext())) {
            return result;
        }

        // Iterate over two queries results and add secondary entities with a matching foreign key.
        String primaryKey = primaryKeyIterator.next();
        Entity secondaryEntity = secondaryEntityIterator.next();
        String secondaryKey = (String) secondaryEntity.getProperty(foreignKey);
        while (primaryKeyIterator.hasNext() && secondaryEntityIterator.hasNext()) {
            if (primaryKey.compareTo(secondaryKey) > 0) {
                // primaryKey > secondaryKey
                secondaryEntity = secondaryEntityIterator.next();
                secondaryKey = (String) secondaryEntity.getProperty(foreignKey);
            } else if (primaryKey.compareTo(secondaryKey) < 0) {
                // primaryKey < secondaryKey
                primaryKey = primaryKeyIterator.next();
            } else {
                // primaryKey = secondaryKey
                result.add(secondaryEntity);
                primaryKey = primaryKeyIterator.next();
                secondaryEntity = secondaryEntityIterator.next();
                secondaryKey = (String) secondaryEntity.getProperty(foreignKey);
            }
        }

        // Add last entity to result (if matching).
        if (primaryKey.compareTo(secondaryKey) == 0) {
            result.add(secondaryEntity);
        }

        return result;
    }
}
