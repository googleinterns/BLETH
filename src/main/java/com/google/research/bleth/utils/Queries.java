package com.google.research.bleth.utils;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    public static double Average(List<Entity> entities, String property) throws ClassCastException {
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
    public static Map<String, Double> Average(List<Entity> entities, Set<String> properties) throws ClassCastException {
        Map<String, Double> resultMap = new HashMap<>(); // Maintains the average for each property.
        Map<String, Integer> countersMap = new HashMap<>(); // Maintains the counter for each property.
        properties.forEach(property -> initializeMaps(resultMap, countersMap, property));
        entities.forEach(entity -> updateMaps(resultMap, countersMap, properties, entity));
        return ImmutableMap.copyOf(resultMap);
    }

    private static void initializeMaps(Map<String, Double> resultMap, Map<String, Integer> countersMap, String property) {
        resultMap.put(property, Double.NaN);
        countersMap.put(property, 0);
    }

    private static void updateMaps(Map<String, Double> resultMap, Map<String, Integer> countersMap,
                                   Set<String> properties, Entity entity) {
        for (String property : entity.getProperties().keySet()) {
            if (properties.contains(property)) {
                int propertyCount = countersMap.get(property); // Property counter excluding current entity.
                double propertyValue = (double) entity.getProperty(property);
                if (propertyCount == 0) {
                    countersMap.put(property, 1);
                    resultMap.put(property, propertyValue);
                } else {
                    // Restore previous sum using old counter and current average, and recompute the average.
                    double currentAverage = resultMap.get(property);
                    countersMap.put(property, propertyCount + 1);
                    resultMap.put(property, (currentAverage * propertyCount + propertyValue) / (propertyCount + 1));
                }
            }
        }
    }
}
