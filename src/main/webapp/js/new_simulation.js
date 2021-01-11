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

/** Strategies Retrieval. */

/**
 * Fetch url and retrieve a JSON object storing all strings representation of 
 * movement and awakeness strategies, and call update form to enable choosing 
 * strategies from a closed list.
 */
function retrieveStrategies() {
    fetch('/list-strategies')
    .then(response => response.json())
    .then(strategies => { 
        updateFormStrategiesList(strategies); 
    });
}

/**
 * Update form datalists to enable choosing strategies from a closed list.
 * @param {String[]} strategies is a JSON object stirng lists of movement and awakeness strategies (as strings).
 */
function updateFormStrategiesList(strategies) {
    updateDatalistValuesFromArray('movementStrategy', strategies.movement);
    updateDatalistValuesFromArray('awakenessStrategy', strategies.awakeness);
}

/**
 * Add values stored in an array to a datalist corresponding to a given id.
 * @param {String} datalistId is the datalist id.
 * @param {String[]} valuesArray is an array of values to be added to the datalist.
 */
function updateDatalistValuesFromArray(datalistId, valuesArray) {
    var valuesHtml = '';
    for (const value of valuesArray) {
        valuesHtml += '<option value="' + value + '" />';
    }

    document.getElementById(datalistId).innerHTML = valuesHtml;
}

/** Inputs' Dynamic Bounds. */

/**
 * Verify that the input tag doesn't exceed its limit.
 * @param {String} fieldId is the bounded element.
 * @param {Number} maxValue is the largest value the element can hold.
 */
function setStaticUpperBound(fieldId, maxValue) {
    var currentValue = document.getElementById(fieldId).value;
    document.getElementById(fieldId).value = Math.min(currentValue, maxValue);
}

/**
 * Verify that the input tag is positive.
 * @param {String} fieldId is the bounded element.
 * @param {Number} minValue is the lowest positive value the element can hold.
 */
function setStaticLowerBound(fieldId, minValue) {
    var currentValue = document.getElementById(fieldId).value;
    document.getElementById(fieldId).value = Math.max(currentValue, minValue);
}

/** Verify that the values of all input tags are legal, change illegal values. */
function setRigidBounds() {
    setStaticLowerBound('roundsNum', 1);
    setStaticLowerBound('beaconsNum', 1);
    setStaticLowerBound('observersNum', 1);

    setStaticLowerBound('rowsNum', 1);
    var maxRows = document.getElementById('rowsNum').max;
    setStaticUpperBound('rowsNum', maxRows);

    setStaticLowerBound('colsNum', 1);
    var maxCols = document.getElementById('colsNum').max;
    setStaticUpperBound('colsNum', maxCols);

    setStaticLowerBound('awakenessCycle', 1);
    setStaticLowerBound('awakenessDuration', 1);
    setStaticLowerBound('transmissionThresholdRadius', 0);
}

/**
 * Given two id's of HTML input tags, dynamically bound the value of the first input tag 
 * with the value of the second input tag.
 * @param {String} boundedInputId is the bounded element.
 * @param {String} boundingInputId is the bounding element.
 */
function setDynamicUpperBound(boundedInputId, boundingInputId) {
    var boundingValue = document.getElementById(boundingInputId).value;
    document.getElementById(boundedInputId).setAttribute("max", boundingValue);
    var currentValue = document.getElementById(boundedInputId).value
    document.getElementById(boundedInputId).value = Math.min(boundingValue, currentValue);
}

/**
 * Verify the following constrains are kept:
 * awakenessCycle <= roundsNum
 * awakenessDuration <= awakenessCycle
 * all input tags' values are legal
 */
function setAllDynamicUpperBounds() {
    setRigidBounds();
    setDynamicUpperBound('awakenessCycle', 'roundsNum');
    setDynamicUpperBound('awakenessDuration', 'awakenessCycle');
}

/** New simulation creation. */

/** Gather parameter for an HTTP request and fetch a servlet to create and run a new simulation. */
function createNewSimulation() {
    // Construct list of form inputs.
    const form = document.getElementById('newSimulationForm');
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

    // If confirmed, fetch url to create and run a new simulation.
    var confirmed = confirm("Create and run a new simulation?")
    if (confirmed) {
        fetch('/enqueue-simulation', {method: 'POST', body: params})
        .then(response => response.text())
        .then(message => window.alert(message));
    }
}
