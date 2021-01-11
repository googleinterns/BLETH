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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/** A factory class to create new observers. */
public class ObserverFactory {
    private int observerId = 0; // used for generating unique id for each observer.

    /**
     * Create new observer, according to the given parameters.
     * @param initialLocation is the location on board where the observer is placed.
     * @param movementStrategy determines how the observer moves.
     * @param resolver is the resolver that the observer belongs to.
     * @param owner is the real board that represents the world in which the observer lives.
     * @param awakenessStrategy determines when the observer is activated.
     * @return
     */
    public Observer createObserver(Location initialLocation, IMovementStrategy movementStrategy, IResolver resolver,
                                   IAgentOwner owner, IAwakenessStrategy awakenessStrategy) {
        checkNotNull(initialLocation);
        checkNotNull(movementStrategy);
        checkNotNull(resolver);
        checkNotNull(owner);
        checkNotNull(awakenessStrategy);
        checkArgument(owner.isLocationValid(initialLocation));

        Observer newObserver = new Observer(observerId++, initialLocation, movementStrategy, resolver, owner, awakenessStrategy);
        owner.updateAgentLocation(null, initialLocation, newObserver);
        return newObserver;
    }
}
