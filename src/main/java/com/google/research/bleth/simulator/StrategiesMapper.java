package com.google.research.bleth.simulator;

import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * A singleton class providing a methods for mapping strings to movement strategy objects and awakeness strategies types.
 * In addition, the strategies mapper class provides methods for generating a list of strings representing all existing
 * movement and awakeness strategies, to be used in the new simulation form.
 */
public class StrategiesMapper {

    private static StrategiesMapper instance = null;
    private HashMap<String, Class<? extends IMovementStrategy>> movement = new HashMap<>();
    private HashMap<String, AwakenessStrategyFactory.Type> awakeness = new HashMap<>();

    /**
     * A static method to create an instance of the strategies mapper class.
     * @return the single instance of the mapper class.
     */
    public static StrategiesMapper getInstance() {
        if (instance == null) {
            instance = new StrategiesMapper();
        }
        return instance;
    }

    /**
     * Create and return a new movement strategy based on the type provided as a string.
     * @param typeAsString a string indicating the type of movement strategy to create.
     * @return a movement strategy object.
     * @throws NoSuchMethodException if the concrete movement strategy does not have a declared constructor.
     * @throws IllegalAccessException if the concrete movement strategy's constructor is not accessible.
     * @throws InstantiationException if the instantiation fails.
     */
    public IMovementStrategy getMovementStrategy(String typeAsString)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return movement.get(typeAsString).getDeclaredConstructor().newInstance();
    }

    /**
     * Return the awakeness strategy type corresponding to the given string.
     * @param typeAsString a string indicating the type of awakeness strategy to return.
     * @return an awakeness strategy type.
     */
    public AwakenessStrategyFactory.Type getAwakenessStrategy(String typeAsString) {
        return awakeness.get(typeAsString);
    }

    /** Return a list of all strings representing all existing movement strategies. */
    public List<String> listMovementStrategies() {
        return new ArrayList<>(movement.keySet());
    }

    /** Return a list of all strings representing all existing awakeness strategies. */
    public List<String> listAwakenessStrategies() {
        return new ArrayList<>(awakeness.keySet());
    }

    private StrategiesMapper() {
        Reflections reflections = new Reflections("com.google.research.bleth.simulator");
        Set<Class<? extends IMovementStrategy>> implementations = reflections.getSubTypesOf(IMovementStrategy.class);
        for (Class<? extends IMovementStrategy> impl : implementations) {
            movement.put(impl.toString(), impl);
        }

        for (AwakenessStrategyFactory.Type type : AwakenessStrategyFactory.Type.values()) {
            awakeness.put(type.toString(), type);
        }
    }
}
