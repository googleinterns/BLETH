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
