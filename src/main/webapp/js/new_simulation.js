// Strategies Retrieval

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

// Inputs' Dynamic Bounds

/** Verify that the values of all input tags are legal, change illegal values. */
function enforceRigidBounds() {
    var roundsNum = document.getElementById('roundsNum').value;
    document.getElementById('roundsNum').value = Math.max(1, roundsNum);

    var beaconsNum = document.getElementById('beaconsNum').value;
    document.getElementById('beaconsNum').value = Math.max(1, beaconsNum);

    var observersNum = document.getElementById('observersNum').value;
    document.getElementById('observersNum').value = Math.max(1, observersNum);

    var currentRows = document.getElementById('rowsNum').value;
    var maxRows = document.getElementById('rowsNum').max;
    document.getElementById('rowsNum').value = Math.max(1, Math.min(maxRows, currentRows));

    var currentCols = document.getElementById('colsNum').value;
    var maxCols = document.getElementById('colsNum').max;
    document.getElementById('colsNum').value = Math.max(1, Math.min(maxCols, currentCols));

    var awakenessCycle = document.getElementById('awakenessCycle').value;
    document.getElementById('awakenessCycle').value = Math.max(1, awakenessCycle);

    var awakenessDuration = document.getElementById('awakenessDuration').value;
    document.getElementById('awakenessDuration').value = Math.max(1, awakenessDuration);

    var radius = document.getElementById('transmissionThresholdRadius').value;
    document.getElementById('transmissionThresholdRadius').value = Math.max(0, radius);
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
    enforceRigidBounds();
    setDynamicUpperBound('awakenessCycle', 'roundsNum');
    setDynamicUpperBound('awakenessDuration', 'awakenessCycle');
}

// New simulation creation.

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
        fetch('/new-simulation', {method: 'POST', body: params})
        .then(response => response.text())
        .then(message => window.alert(message));
    }
}
