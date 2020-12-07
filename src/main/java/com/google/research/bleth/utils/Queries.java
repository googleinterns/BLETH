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

package com.google.research.bleth.utils;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.common.collect.Iterables;
import com.google.research.bleth.simulator.Schema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/** A utility class providing method for db related operations. */
public class Queries {
    private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

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
    public static List<Entity> join(String primaryEntityKind, String secondaryEntityKind,
                                    String foreignKey, Optional<Query.Filter> primaryEntityFilter) {
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
                secondaryEntity = secondaryEntityIterator.next();
                secondaryKey = (String) secondaryEntity.getProperty(foreignKey);
            } else if (primaryKey.compareTo(secondaryKey) < 0) {
                primaryKey = primaryKeyIterator.next();
            } else {
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

    /**
     * Return the average of entities property value. Does not include entities which don't have property.
     * @param entities is a list of entities.
     * @param property is the property to average.
     * @return The average value (or Nan if entities is empty or no entity with value exists).
     * @throws ClassCastException if property value cannot be casted to double (for some entity).
     */
    public static double average(List<Entity> entities, String property) throws ClassCastException {
        return entities.stream()
                .filter(entity -> entity.hasProperty(property))
                .mapToDouble(entity -> (double) entity.getProperty(property))
                .average()
                .orElse(Double.NaN);
    }

    /**
     * Return the average of entities multiple properties values (an average for each property).
     * For each property, include only entities which have the property.
     * @param entities is a list of entities.
     * @param properties is a list of properties.
     * @return a map where keys indicating properties' names and values indicating the averages (for each entry, Nan
     * value indicating no entity with property exists within entities list).
     * @throws ClassCastException if one of properties values cannot be casted to double (for some entity).
     */
    public static Map<String, Double> average(List<Entity> entities, Set<String> properties) throws ClassCastException {
        return properties.stream()
                .collect(toImmutableMap(Function.identity(), property -> average(entities, property)));
    }

    /**
     * Given a simulationId as a string, delete all data associated with that simulation id.
     * @param simulationId is the simulation id.
     */
    public static void delete(String simulationId) {
        // Specify cross-group transaction.
        TransactionOptions options = TransactionOptions.Builder.withXG(true);
        Transaction deleteTransaction = datastore.beginTransaction(options);
        try {
            delete(Schema.StatisticsState.entityKindBeaconsObservedPercent, Schema.StatisticsState.simulationId, simulationId);
            delete(Schema.StatisticsState.entityKindDistance, Schema.StatisticsState.simulationId, simulationId);
            delete(Schema.BoardState.entityKindReal, Schema.BoardState.simulationId, simulationId);
            delete(Schema.BoardState.entityKindEstimated, Schema.BoardState.simulationId, simulationId);
            datastore.delete(KeyFactory.stringToKey(simulationId));
            deleteTransaction.commit();
        } finally {
            if (deleteTransaction.isActive()) {
                deleteTransaction.rollback();
            }
        }
    }

    private static void delete(String entityKind, String foreignKeyProperty, String foreignKeyKeyValue) {
        Query.Filter filter = new Query.FilterPredicate(foreignKeyProperty, Query.FilterOperator.EQUAL, foreignKeyKeyValue);
        Query entitiesToDelete = new Query(entityKind).setFilter(filter);
        Iterable<Entity> entitiesToDeleteIterable = datastore.prepare(entitiesToDelete).asIterable();
        datastore.delete(Iterables.transform(entitiesToDeleteIterable, Entity::getKey));
    }
}
