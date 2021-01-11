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

import { toQueryString } from './utils.js';

window.retrieveSimulations = retrieveSimulations; // Add function to global scope.
const ASC = 'ASCENDING';
const DESC = 'DESCENDING';
var currentSortProperty = null;
var currentSortDirection = null;

/**
 * Fetch url and retrieve a JSON object storing simulations' metadata,
 * and display as an html table.
 */
function retrieveSimulations() {
    fetch('/list-simulations')
    .then(response => response.json())
    .then(simulations => { 
        displaySimulationAsTable(simulations); 
    });
}

/**
 * Fetch url and retrieve a JSON object storing sorted simulations' metadata,
 * and display as an html table.
 * @param {String} sortProperty is the name of the property to sort by.
 * @param {String} sortDirection is the sort direction (0 for ascending, 1 for descending).
 */
function retrieveSortedSimulations(sortProperty, sortDirection) {
    const queryString = toQueryString({ sortProperty : sortProperty, sortDirection : sortDirection });
    fetch(`/list-simulations?${queryString}`)
    .then(response => response.json())
    .then(simulations => { 
        displaySimulationAsTable(simulations); 
    });
}

/**
 * Given a json object storing simulations' metadata, write all data to an html table.
 * @param {Object} simulations is an object storing simulations' unique id and metadata.
 */
function displaySimulationAsTable(simulations) {
    const allSimulationIds = Object.keys(simulations);
    if (allSimulationIds.length === 0) { return; }
    
    const firstSimulationId = allSimulationIds[0];
    const firstSimulation = simulations[firstSimulationId];
    const simulationProperties = Object.keys(firstSimulation);

    var table = document.getElementById("simulations-table");
    table.innerHTML = '';
    addSimulationHeader(table, simulationProperties);
    addSimulationRows(table, simulations);
}

/**
 * Given a table and an array of strings, add a header based on the array's items.
 * @param {HTMLElement} table is the html table to be updated.
 * @param {String[]} properties is an array of header properties.
 */
function addSimulationHeader(table, properties) {
    var header = table.createTHead();
    var row = header.insertRow(0);
    // cell 0 contains the simulation button, therefore header starts at cell 1.
    row.insertCell(0).innerHTML = '';
    for (var i = 0; i < properties.length; i++) {
        row.insertCell(i + 1).appendChild(createSortButton(properties[i]));
    }
}

/**
 * Given a table and an object storing simulations' metadata, add a row for each simulation.
 * @param {HTMLElement} table is the table to be updated.
 * @param {Object} simulations is an object storing simulations' id and metadata.
 */
function addSimulationRows(table, simulations) {
    for (const id in simulations) {
        const simulation = simulations[id];
        var row = table.insertRow(-1);
        const visualizeSimulationButton = createVisualizationButton(simulation, id);
        const deleteSimulationButton = createDeletionButton(id);
        const cell = row.insertCell(0);
        cell.appendChild(visualizeSimulationButton);
        cell.appendChild(deleteSimulationButton);
        var i = 1; // cell index (cell 0 is a button).
        for (const property in simulation) {
            row.insertCell(i++).innerHTML = simulation[property];
        }
    }
}

/**
 * Create a button for simulation visualization.
 * @param {Object} simulation is the simulation object.
 * @param {String} id is the simulation id. 
 */
function createVisualizationButton(simulation, id) {
    var visualizeSimulationButton = document.createElement('button');
    visualizeSimulationButton.innerText = 'Visualize Simulation';
    visualizeSimulationButton.addEventListener('click', () => {
        var simulationWithId = JSON.parse(JSON.stringify(simulation)); // Deep copy.
        simulationWithId['id'] = id;
        window.location.replace('simulation_visualization.html?' + toQueryString(simulationWithId));
    });

    return visualizeSimulationButton;
}

/**
 * Create a button for simulation deletion.
 * @param {String} id is the simulation id. 
 */
function createDeletionButton(id) {
    var deleteSimulationButton = document.createElement('button');
    deleteSimulationButton.innerText = 'Delete Simulation';
    deleteSimulationButton.addEventListener('click', () => {
        var confirmed = confirm('Do you want to delete this simulation?');
        if (confirmed) {
            var params = new URLSearchParams();
            params.append('simulationId', id);
            fetch('/delete-simulation', {method: 'POST', body: params})
            .then(response => response.text())
            .then(message => window.alert(message))
            .then(() => location.reload())
        }
    });

    return deleteSimulationButton;
}

/**
 * Create a button for simulations sort.
 * @param {String} sortProperty is the name of the property to sort by.
 */
function createSortButton(sortProperty) {
    var sortButton = document.createElement('button');
    sortButton.innerText = sortProperty;
    sortButton.classList.add('sort-button');
    sortButton.addEventListener('click', () => {
        if (currentSortProperty === sortProperty) {
            currentSortDirection = currentSortDirection === ASC ? DESC : ASC;
        } else {
            currentSortProperty = sortProperty;
            currentSortDirection = ASC;
        }
        retrieveSortedSimulations(currentSortProperty, currentSortDirection);
    });

    return sortButton;
}