package com.google.research.bleth.simulator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;

public final class StrategiesMapperTest {

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

    @Test
    public void getMovementStrategyByStationaryMovementStrategyTypeAsString_shouldGetStationaryMovementStrategyType() {
        StrategiesMapper mapper = StrategiesMapper.getInstance();
        MovementStrategyFactory.Type strategy = MovementStrategyFactory.Type.STATIONARY;
        String strategyAsString = strategy.toString();

        assertThat(mapper.getMovementStrategy(strategyAsString)).isEqualTo(MovementStrategyFactory.Type.STATIONARY);
    }

    @Test
    public void getMovementStrategyByRandomMovementStrategyTypeAsString_shouldGetRandomMovementStrategyType() {
        StrategiesMapper mapper = StrategiesMapper.getInstance();
        MovementStrategyFactory.Type strategy = MovementStrategyFactory.Type.RANDOM;
        String strategyAsString = strategy.toString();

        assertThat(mapper.getMovementStrategy(strategyAsString)).isEqualTo(MovementStrategyFactory.Type.RANDOM);
    }
}
