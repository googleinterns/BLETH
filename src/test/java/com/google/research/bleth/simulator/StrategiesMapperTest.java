package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public final class StrategiesMapperTest {

    @Test
    public void getMovementStrategyByStationaryMovementStrategyAsString_shouldGetStationaryMovementStrategyObject()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        StrategiesMapper mapper = StrategiesMapper.getInstance();
        String strategyAsString = StationaryMovementStrategy.class.toString();

        IMovementStrategy retrievedStrategy = mapper.getMovementStrategy(strategyAsString);

        assertThat(retrievedStrategy instanceof StationaryMovementStrategy).isTrue();
    }

    @Test
    public void getMovementStrategyByRandomMovementStrategyAsString_shouldGetRandomMovementStrategyObject()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        StrategiesMapper mapper = StrategiesMapper.getInstance();
        String strategyAsString = RandomMovementStrategy.class.toString();

        IMovementStrategy retrievedStrategy = mapper.getMovementStrategy(strategyAsString);

        assertThat(retrievedStrategy instanceof RandomMovementStrategy).isTrue();
    }

    @Test
    public void generateTwoRandomMovementStrategies_shouldNotGenerateTheSameObject()
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        StrategiesMapper mapper = StrategiesMapper.getInstance();
        String strategyAsString = RandomMovementStrategy.class.toString();

        IMovementStrategy firstRetrievedStrategy = mapper.getMovementStrategy(strategyAsString);
        IMovementStrategy secondRetrievedStrategy = mapper.getMovementStrategy(strategyAsString);

        assertThat(firstRetrievedStrategy).isNotEqualTo(secondRetrievedStrategy);
    }

    @Test
    public void getAwakenessStrategyByFixedAwakenessStrategyTypeAsString_shouldGetFixedAwakenessStrategyType() {
        StrategiesMapper mapper = StrategiesMapper.getInstance();
        AwakenessStrategyFactory.Type strategy = AwakenessStrategyFactory.Type.FIXED;
        String strategyAsString = strategy.toString();

        assertThat(mapper.getAwakenessStrategy(strategyAsString)).isEqualTo(AwakenessStrategyFactory.Type.FIXED);
    }

    @Test
    public void getAwakenessStrategyByRandomAwakenessStrategyTypeAsString_shouldGetRandomAwakenessStrategyType() {
        StrategiesMapper mapper = StrategiesMapper.getInstance();
        AwakenessStrategyFactory.Type strategy = AwakenessStrategyFactory.Type.RANDOM;
        String strategyAsString = strategy.toString();

        assertThat(mapper.getAwakenessStrategy(strategyAsString)).isEqualTo(AwakenessStrategyFactory.Type.RANDOM);
    }
}
