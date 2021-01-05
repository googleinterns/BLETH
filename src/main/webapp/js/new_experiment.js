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

/** Gather parameter for an HTTP request and fetch a servlet to create and run a new experiment. */
function createNewExperiment() {
    // Construct list of form inputs.
    const form = document.getElementById('newExperimentForm');
    const inputArray = [...form.getElementsByTagName('input')];
    const params = new URLSearchParams();

    // Validate all required fields are provided and construct POST request parameters.
    for (const input of inputArray) {
        if (input.value === '') {
            window.alert('Please fill all missing values.')
            return;
        }
        params.append(input.id, input.value); 
    }

    // If confirmed, fetch url to create and run a new experiment.
    var confirmed = confirm("Create and run a new experiment?")
    if (confirmed) {
        fetch('/enqueue-experiment', {method: 'POST', body: params})
        .then(response => response.text())
        .then(message => window.alert(message));
    }
}