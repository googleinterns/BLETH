import { toQueryString, getUrlVars, sleep } from './utils.js';

window.visualize = visualize; // Add function to global scope.

/**
 * Get the simulation metadata encoded as a query string, iterate over all rounds and visualize
 * the real and estimated board state at each round (with a 1000 ms delay between two consecutive rounds).
 */
async function visualize() {
    const realBoardElementId = 'real-board-table';
    const estimatedBoardElementId = 'estimated-board-table';
    var simulation = getUrlVars();
    var mainHeader = document.getElementById('simulation-visualization-header');
    var roundHeader = document.getElementById('current-round-header');
    mainHeader.innerText = 'Visualizing Simulation\n' + simulation.id;
    var params = {};
    var queryString;
    params['simulationId'] = simulation.id;
    for (var round = 0; round < simulation.roundsNum; round++) {
        roundHeader.innerText = 'Current Round: ' + round;
        params['round'] = round;

        // Request real board state.
        params['isReal'] = true;
        queryString = toQueryString(params);
        fetch(`/read-board-state?${queryString}`)
        .then(response => response.json())
        .then(boardState => visualizeBoardState(boardState.array, realBoardElementId));

        // Request estimated board state.
        params['isReal'] = false;
        queryString = toQueryString(params);
        fetch(`/read-board-state?${queryString}`)
        .then(response => response.json())
        .then(boardState => visualizeBoardState(boardState.array, estimatedBoardElementId));

        // Delay.
        await sleep(1000);
    }
}

/**
 * Given a boars state and an id of an html table element, update the table to display the board state.
 * @param {Array} board is the board state to visualize. 
 * @param {*} tableId is the id of the html table element to be updated.
 */
function visualizeBoardState(board, tableId) {

    // Set table and table body
    var gameBoardTable = document.getElementById(tableId);
    var gameBoardTableBody = document.createElement('tbody');

    // Clear last state of table
    gameBoardTable.innerHTML = '';

    // Change cells' classes according to board.
    board.forEach(function(rowData) {

        var row = document.createElement('tr');
        row.classList.add('game-board-tr')

        rowData.forEach(function(cellData) {
            var cell = document.createElement('td');
            cell.classList.add('game-board-td');
            cell.classList.add(determineCellClass(cellData));
            row.appendChild(cell);
        });

        gameBoardTableBody.appendChild(row);
    });

    gameBoardTable.appendChild(gameBoardTableBody);
    document.body.appendChild(gameBoardTable);
}

/**
 * return the css class name representing the correct state of the cell (empty/contains beacons/observers only).
 * @param {Array} agents a list of agents ids located in the same cell of the board.
 */
function determineCellClass(agents) {
    if (agents.length === 0) { return 'game-board-td-empty'; }
    for (var i = 0; i < agents.length; i++) {
        if (agents[i].charAt(0) === 'B') { return 'game-board-td-contains-beacon'; }
    }
    return 'game-board-td-observers-only';
}