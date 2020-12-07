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

package com.google.research.bleth.simulator;

/** Represent an observer. */
public interface IObserver extends IAgent {

    /**
     * Add a transmission to the transmissions the observer observed so far in the current round.
     * @param beaconTransmission the new transmission the observer observe.
     */
    void observe(Transmission beaconTransmission);

    /**
     * Deliver all the transmissions the observer observed in the current round to its resolver.
     */
    void passInformationToResolver();

    /** Returns true if the observer is active, false otherwise. */
    boolean isAwake();
}