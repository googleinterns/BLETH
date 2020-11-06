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
 * @param {Array} strategies is a JSON object stirng lists of movement and awakeness strategies (as strings).
 */
function updateFormStrategiesList(strategies) {
    movementStrategies = strategies.movement;
    awakenessStrategies = strategies.awakeness;
    updateDatalistValuesFromArray('beaconMovementStrategy', movementStrategies);
    updateDatalistValuesFromArray('observerMovementStrategy', movementStrategies);
    updateDatalistValuesFromArray('observerAwakenessStrategy', awakenessStrategies);
}

/**
 * Add values stored in an array to a datalist corresponding the a given id.
 * @param {String} datalistId is the datalist id.
 * @param {Array} valuesArray is an array of values to be added to the datalist.
 */
function updateDatalistValuesFromArray(datalistId, valuesArray) {
    var valuesHtml = '';
    for (const value of valuesArray) {
        valuesHtml += '<option value="' + value + '" />';
    }

    document.getElementById(datalistId).innerHTML = valuesHtml;
}

// Inputs' Dynamic Upper Boundes

/**
 * Given two id's of HTML input tags, dynamically bound the value of the first input tag 
 * with the value of the second input tag.
 * @param {String} boundedInputId is the bounded element.
 * @param {String} boundingInputId is the bounding element.
 */
function setDynamicUpperBound(boundedInputId, boundingInputId) {
    var boundingValue = document.getElementById(boundingInputId).value;
    var boundedValue = document.getElementById(boundedInputId).value;

    document.getElementById(boundedInputId).setAttribute("max", boundingValue);

    if (boundedValue > boundingValue) {
        document.getElementById(boundedInputId).value = boundingValue;
    }
}

/**
 * Verify the following constrains are kept:
 * awakenessCycle <= roundsNum
 * awakenessDuration <= awakenessCycle
 */
function setAllDynamicUpperBounds() {
    setDynamicUpperBound('awakenessCycle', 'roundsNum');
    setDynamicUpperBound('awakenessDuration', 'awakenessCycle');
}