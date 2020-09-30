/**
 * Write hard-coded board states for db.
 */
function writeHardCodedBoardStates() {
    fetch('/write-hard-coded-board-state');
}

/**
 * Display 10 rounds of a dummy hard-coded simultaion, both real and estimated board.
 */
async function fetchHardCodedBoardStates() {

    var simulationId = "demo-tracing-sim-1";

    for (var roundNumber = 0; roundNumber < 10; roundNumber++) {
        // Set params to POST request.
        const params = new URLSearchParams();
        params.append('simulationId', 'demo-tracing-sim-1');
        params.append('simulationId', simulationId);
        params.append('round', roundNumber);

        // Fetch and display real board state.
        fetch('/read-hard-coded-real-board-state', {method: 'POST', body: params})
        .then(response => response.json())
        .then(boardState => visualizeHardCodedRealBoardState(boardState.array));

        // Fetch and display estimated board state.
        fetch('/read-hard-coded-estimated-board-state', {method: 'POST', body: params})
        .then(response => response.json())
        .then(boardState => visualizeHardCodedEstimatedBoardState(boardState.array));

        await sleep(1000);
    }
}

/**
 * JS sleep method implementation.
 * @param {number} ms number of milliseconds to sleep.
 */
function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

/**
 * Visualize the board state on index.html page.
 * @param {boardState} board a JSON string representing the board state in a certain round of the simulation.
 * @param {boolean} tableId  the id of the table element to be updated (real or estimated table on index.html).
 */
function visualizeHardCodedBoardState(board, tableId) {

    // Set table and table body
    var gameBoardTable = document.getElementById(tableId);
    var gameBoardTableBody = document.createElement('tbody');

    // Clear last state of table
    gameBoardTable.innerHTML = '';

    // Change cells' colors according to board.
    board.forEach(function(rowData) {

        var row = document.createElement('tr');
        row.classList.add('game-board-tr')

        rowData.forEach(function(cellData) {
            var cell = document.createElement('td');
            cell.classList.add('game-board-td')
            cell.style.backgroundColor = determineColor(cellData);
            row.appendChild(cell);
        });

        gameBoardTableBody.appendChild(row);
    });

    gameBoardTable.appendChild(gameBoardTableBody);
    document.body.appendChild(gameBoardTable);  
}

/**
 * Visualize the real board state on index.html page.
 * @param {boardState} board a JSON string representing the board state in a certain round of the simulation.
 */
function visualizeHardCodedRealBoardState(board) {
    visualizeHardCodedBoardState(board, 'real-game-board');
}

/**
 * Visualize the estimated board state on index.html page.
 * @param {boardState} board a JSON string representing the board state in a certain round of the simulation.
 */
function visualizeHardCodedEstimatedBoardState(board) {
    visualizeHardCodedBoardState(board, 'estimated-game-board');
}

/**
 * return 'white' is cell is empty, 'blue' if contains observers only and 'red' otherwise.
 * @param {agents list} agents a list of agents ids located in the same cell of the board.
 */
function determineColor(agents) {
    if (agents.length === 0) { return 'white'; }
    for (var i = 0; i < agents.length; i++) {
        if (agents[i].charAt(0) === 'B') { return 'red'; }
    }
    return 'blue';
}