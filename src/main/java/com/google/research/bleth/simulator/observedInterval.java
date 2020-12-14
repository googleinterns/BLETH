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

import com.google.auto.value.AutoValue;

/** An interval of time which a beacon has or hasn't been observed. */
@AutoValue
public abstract class observedInterval {

    public abstract int start();
    public abstract int end();
    public abstract boolean observed();
    /** Returns the duration of the interval. */
    public int duration(){
        return end() - start() + 1;
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setStart(int start);
        public abstract Builder setEnd(int end);
        public abstract Builder setObserved(boolean observed);
        public abstract boolean observed();
        public abstract observedInterval build();
    }
}
