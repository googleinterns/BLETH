package com.google.research.bleth.simulator;

import java.util.Random;

/**
 * A class for generating strategies based on awakeness cycle and duration, and strategy type provided.
 * The factory gets as an argument the specific type of strategy to generate.
 * The factory sets these constants for each generated strategy, and generates random parameters (if such exists).
 */
public class AwakenessStrategyFactory {

    public enum Type {
        FIXED, RANDOM;
    }

    private Random rand = new Random(); // used for generating the initial awakeness time
    private Type type;

    public AwakenessStrategyFactory(Type type) {
        this.type = type;
    }

    /**
     * Create a new strategy according to the factory's type attribute and the following params:
     * @param awakenessCycle is the cycle, which is the number of rounds in which every observer
     * must have an awakeness period.
     * @param awakenessDuration is the duration, which is the number of rounds in which an observer
     * is awake in a single awakeness cycle.
     * @return an awakeness strategy initialized with all required parameters (if no proper type was set, return random by default).
     */
    public IAwakenessStrategy createStrategy(int awakenessCycle, int awakenessDuration) {
        switch (this.type) {
            case FIXED: return createFixedStrategy(awakenessCycle, awakenessDuration);
            default: return createRandomStrategy(awakenessCycle, awakenessDuration);
        }
    }

    private IAwakenessStrategy createFixedStrategy(int awakenessCycle, int awakenessDuration) {
        int firstAwakenessTime = rand.nextInt(awakenessCycle - awakenessDuration);
        return new FixedAwakenessStrategy(awakenessCycle, awakenessDuration, firstAwakenessTime);
    }

    private IAwakenessStrategy createRandomStrategy(int awakenessCycle, int awakenessDuration) {
        int firstAwakenessTime = rand.nextInt(awakenessCycle - awakenessDuration);
        return new RandomAwakenessStrategy(awakenessCycle, awakenessDuration, firstAwakenessTime);
    }
}