import { toQueryString, getUrlVars, sleep } from './utils.js';

window.visualize = visualize; // Add function to global scope.

async function visualize() {
    var simulation = getUrlVars();
    var header = document.getElementById('simulation-visualization-header');
    header.innerText = 'Visualizing Simulation\n' + simulation.id;
    var params = {};
    var queryString;
    params['simulationId'] = simulation.id;
    for (var round = 0; round < simulation.roundsNum; round++) {
        params['round'] = round;

        // Request real board state.
        params['isReal'] = true;
        queryString = toQueryString(params);
        fetch(`/read-board-state?${queryString}`)
        .then(response => response.json())
        .then(boardState => console.log(boardState.array));

        // Request estimated board state.
        params['isReal'] = false;
        queryString = toQueryString(params);
        fetch(`/read-board-state?${queryString}`)
        .then(response => response.json())
        .then(boardState => console.log(boardState.array));

        // Delay.
        await sleep(1000);
    }
}
