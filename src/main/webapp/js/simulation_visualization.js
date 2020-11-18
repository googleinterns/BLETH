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
        await fetch(`/read-board-state?${queryString}`)
        .then(response => response.json())
        .then(boardState => visualizeBoardState(boardState.array, realBoardElementId));

        // Request estimated board state.
        params['isReal'] = false;
        queryString = toQueryString(params);
        await fetch(`/read-board-state?${queryString}`)
        .then(response => response.json())
        .then(boardState => visualizeBoardState(boardState.array, estimatedBoardElementId));

        // Delay.
        await sleep(1000);
    }
}

/**
 * Given a board state and an id of an html table element, update the table to display the board state.
 * @param {String[][]} board is the board state to visualize. 
 * @param {String} tableId is the id of the html table element to be updated.
 */
function visualizeBoardState(board, tableId) {

    // Set table and table body
    var boardTable = document.getElementById(tableId);
    var boardTableBody = document.createElement('tbody');

    // Clear last state of table
    boardTable.innerHTML = '';

    // Change cells' classes according to board.
    board.forEach(function(rowData) {

        var row = document.createElement('tr');
        row.classList.add('board-tr')

        rowData.forEach(function(cellData) {
            var cell = document.createElement('td');
            cell.classList.add('board-td');
            cell.classList.add(determineCellClass(cellData));
            row.appendChild(cell);
        });

        boardTableBody.appendChild(row);
    });

    boardTable.appendChild(boardTableBody);
    document.body.appendChild(boardTable);
}

/**
 * return the css class name representing the correct state of the cell (empty/contains beacons/observers only).
 * @param {String[]} agents a list of agents ids located in the same cell of the board.
 * @returns {String} the correct css class name.
 */
function determineCellClass(agents) {
    if (agents.length === 0) { return 'board-td-empty'; }
    for (var i = 0; i < agents.length; i++) {
        if (agents[i].charAt(0) === 'B') { return 'board-td-contains-beacon'; }
    }
    return 'board-td-observers-only';
}