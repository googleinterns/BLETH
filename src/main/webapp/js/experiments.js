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

window.retrieveExperiments = retrieveExperiments; // Add function to global scope.

/**
 * Fetch url and retrieve a JSON object storing experiments,
 * and display as an html table.
 */
function retrieveExperiments() {
    fetch('/list-experiments')
    .then(response => response.json())
    .then(experiments => { 
        displayExperimentsAsTable(experiments); 
    });
}

/**
 * Given a json object storing experiments, write all data to an html table.
 * @param {Object} experiments is an object storing experiments' unique id and title.
 */
function displayExperimentsAsTable(experiments) {
    const allExperimentsIds = Object.keys(experiments);
    if (allExperimentsIds.length === 0) { return; }

    var table = document.getElementById("experiments-table");
    table.innerHTML = '';
    var header = table.createTHead();
    var row = header.insertRow(0);
    row.insertCell(0).innerHTML = '';
    row.insertCell(1).innerHTML = 'Experiment ID';
    row.insertCell(2).innerHTML = 'Experiment Title';

    for (const id in experiments) {
        const title = experiments[id];
        var row = table.insertRow(-1);
        const cell = row.insertCell(0);
        const exportDataButton = createExportDataButton(id); 
        cell.appendChild(exportDataButton);
        row.insertCell(1).innerHTML = id;
        row.insertCell(2).innerHTML = title;
    }
}

/**
 * Create a button for exporting raw intervals data.
 * @param {String} id is the experiment id to export.
 */
function createExportDataButton(id) {
    var exportDataButton = document.createElement('button');
    exportDataButton.innerText = 'Export';
    exportDataButton.addEventListener('click', () => {
        // TODO: fetch interval stats and download file.
    });
    return exportDataButton;
}