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

package com.google.research.bleth.simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A singleton class providing a methods for mapping strings to movement strategy objects and awakeness strategies types.
 * In addition, the strategies mapper class provides methods for generating a list of strings representing all existing
 * movement and awakeness strategies, to be used in the new simulation form.
 */
public class StrategiesMapper {

    private static StrategiesMapper instance = null;
    private HashMap<String, MovementStrategyFactory.Type> movement = new HashMap<>();
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
     * Return the movement strategy type corresponding to the given string.
     * @param typeAsString a string indicating the type of movement strategy to return.
     * @return a movement strategy type.
     */
    public MovementStrategyFactory.Type getMovementStrategy(String typeAsString) {
        return movement.get(typeAsString);
    }

    /**
     * Return the awakeness strategy type corresponding to the given string.
     * @param typeAsString a string indicating the type of awakeness strategy to return.
     * @return an awakeness strategy type.
     */
    public AwakenessStrategyFactory.Type getAwakenessStrategy(String typeAsString) {
        return awakeness.get(typeAsString);
    }

    /** Return a list of all strings representing all existing movement strategies, excluding test-only strategies. */
    public static List<String> listMovementStrategies() {
        List<MovementStrategyFactory.Type> allMovementStrategies = Arrays.asList(MovementStrategyFactory.Type.values());
        return allMovementStrategies.stream()
                .filter(type -> !type.isForTest())
                .map(Enum::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /** Return a list of all strings representing all existing awakeness strategies, excluding test-only strategies. */
    public static List<String> listAwakenessStrategies(){
        List<AwakenessStrategyFactory.Type> allAwakenessStrategies = Arrays.asList(AwakenessStrategyFactory.Type.values());
        return allAwakenessStrategies.stream()
                .filter(type -> !type.isForTest())
                .map(Enum::toString)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private StrategiesMapper() {
        for (MovementStrategyFactory.Type type : MovementStrategyFactory.Type.values()) {
            movement.put(type.toString(), type);
        }

        for (AwakenessStrategyFactory.Type type : AwakenessStrategyFactory.Type.values()) {
            awakeness.put(type.toString(), type);
        }
    }
}
