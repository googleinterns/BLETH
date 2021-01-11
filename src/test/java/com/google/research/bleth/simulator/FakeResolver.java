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

import java.util.List;

/**
 * A fake implementation for IResolver that allows to test the arguments it receives when an observer calls receiveInformation.
 */
class FakeResolver implements IResolver {
    private Location observerLocation;
    private List<Transmission> transmissions;

    @Override
    public void receiveInformation(Location observerLocation, List<Transmission> transmissions) {
        this.observerLocation = observerLocation;
        this.transmissions = transmissions;
    }

    public Location getObserverLocation() {
        return observerLocation;
    }

    public List<Transmission> getTransmissions() {
        return transmissions;
    }

    public AbstractBoard getBoard() {
        return null;
    }
}
