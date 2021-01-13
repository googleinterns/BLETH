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
 * Fetch url and retrieve a JSON object storing simulations' metadata and statistics.
 * @param {String} experimentId is the experiment id.
 * @param {String} experimentTitle is the experiment title.
 */
function retrieveExperimentStats(experimentId, experimentTitle) {
    fetch(`/read-experiment-stats?experimentId=${experimentId}`)
    .then(response => response.json())
    .then(stats => { 
        downloadJson(stats, experimentTitle); 
    });
}

/**
 * Download a JSON object as a .json file.
 * @param {Object} exportObj is the JSON object to download.
 * @param {String} exportName is the name of the downloaded file (followed by '.json').
 */
function downloadJson(exportObj, exportName) {
    var data = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(exportObj));
    var anchor = document.createElement('a');
    anchor.setAttribute("href", data);
    anchor.setAttribute("download", exportName + ".json");
    document.body.appendChild(anchor);
    anchor.click();
    anchor.remove();
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
        const exportDataButton = createExportDataButton(id, title); 
        cell.appendChild(exportDataButton);
        row.insertCell(1).innerHTML = id;
        row.insertCell(2).innerHTML = title;
    }
}

/**
 * Create a button for exporting raw intervals data.
 * @param {String} id is the experiment id to export.
 * @param {String} title is the experiment title to export.
 */
function createExportDataButton(id, title) {
    var exportDataButton = document.createElement('button');
    exportDataButton.innerText = 'Download';
    exportDataButton.addEventListener('click', () => {
        retrieveExperimentStats(id, title);
    });
    return exportDataButton;
}