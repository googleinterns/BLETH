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

import java.util.Map;

/** Represent a trusted cloud global resolver. */
public interface IGlobalResolver extends IResolver {

    /** Update the estimated board which the resolver holds based on its current state and the information from the observers. */
    void estimate();

    /** Returns the estimated board that the resolver contains. */
    EstimatedBoard getBoard();

    /** Returns the map between each beacon and its estimated location. */
    Map<Beacon, Location> getBeaconsToEstimatedLocations();
}
