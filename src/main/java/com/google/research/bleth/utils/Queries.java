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

/** A utility class providing method for db related operations, such as join and group by. */
public class Queries {

    /**
     * Given a primary entity kind, a secondary entity kind, a foreign key and a filter, return a list of all entities
     * of the secondary entity matching an entity of the primary entity kind.
     *
     * Equivalent SQL syntax:
     *
     * SELECT secondaryEntityKind.a_1, ... secondaryEntityKind.a_n
     * FROM primaryEntityKind INNER JOIN secondaryEntityKind
     * ON primaryEntityKind.__key__ = secondaryEntityKind.foreignKey
     * WHERE primaryEntityFilter
     *
     * @param primaryEntityKind is the primary entity kind.
     * @param secondaryEntityKind is the secondary entity kind.
     * @param foreignKey is the join property of secondaryEntityKind (the join property of primaryEntityKind is the key).
     * @param primaryEntityFilter is a simple or composed filter to apply on primaryEntityKind prior to the join operation (optional).
     * @return a list of secondary entities matching the primary entities retrieved.
     */
    public static List<Entity> Join(String primaryEntityKind, String secondaryEntityKind,
                                    String foreignKey, Optional<Query.Filter> primaryEntityFilter) {
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        List<Entity> result = new ArrayList<>();

        // Retrieve all primaryEntity keys ordered by their key, filtered by primaryEntityFilter (if provided).
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
        String secondaryKey = String.valueOf(secondaryEntity.getProperty(foreignKey));
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
