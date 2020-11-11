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
 * Given a json object storing simulations' metadata, write all data to an html table.
 * @param simulations is an object storing simulations' unique id and metadata.
 */
function displaySimulationAsTable(simulations) {
    const allSimulationIds = Object.keys(simulations);
    if (allSimulationIds.length === 0) { return; }
    
    const firstSimulationId = allSimulationIds[0];
    const firstSimulation = simulations[firstSimulationId];
    const simulationProperties = Object.keys(firstSimulation);

    var table = document.getElementById("simulations-table");
    addSimulationHeader(table, simulationProperties);
    addSimulationRows(table, simulations);
}

/**
 * Given a table and an array of strings, add a header based on the array's items.
 * @param table is the html table to be updated.
 * @param properties is an array of header properties.
 */
function addSimulationHeader(table, properties) {
    var header = table.createTHead();
    var row = header.insertRow(0);
    // cell 0 contains the simulation button, therefore header starts at cell 1.
    row.insertCell(0).innerHTML = '';
    for (var i = 0; i < properties.length; i++) {
        row.insertCell(i + 1).innerHTML = properties[i];
    }
}

/**
 * Given a table and an object storing simulations' metadata, add a row for each simulation.
 * @param table is the table to be updated.
 * @param simulations is an object storing simulations' id and metadata.
 */
function addSimulationRows(table, simulations) {
    for (const id in simulations) {
        const simulation = simulations[id];
        var row = table.insertRow(-1);
        
        var visualizeSimulationButton = document.createElement('button');
        visualizeSimulationButton.innerText = 'Visualize Simulation';
        visualizeSimulationButton.addEventListener('click', () => {
            // todo: call a method for simulation visualizing.
        });

        row.insertCell(0).appendChild(visualizeSimulationButton);
        var i = 1; // cell index (cell 0 is a button).
        for (const property in simulation) {
            row.insertCell(i++).innerHTML = simulation[property];
        }
    }
}