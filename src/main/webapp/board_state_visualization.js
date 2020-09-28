/**
 * Fetch the hard coded board state servlet to display a hard coded board state on index.html page.
 */
function fetchHardCodedBoardState() {
    fetch('/hard-coded-board-state')
    .then(response => response.json())
    .then(boardState => visualizeHardCodedBoardState(boardState.array, true));
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
 * @param {boolean} isReal   if 'true' visualize the real board, otherwise visualize the estimated board.
 */
function visualizeHardCodedBoardState(board, isReal) {

    console.log(board);

    // Set table and table body
    if (isReal) { var gameBoardTable = document.getElementById('real-game-board'); }
    else { var gameBoardTable = document.getElementById('estimated-game-board'); }
    var gameBoardTableBody = document.createElement('tbody');

    // Clear last state of table
    gameBoardTable.innerHTML = '';

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
 * return 'white' is cell is empty, 'blue' if contains observers only and 'red' otherwise.
 * @param {agents list} agents a list of agents ids located in the same cell of the board.
 */
function determineColor(agents) {
    if (agents.length === 0) { return 'white'; }
    var hasBeacon = false;
    var hasObsrver = false;
    for (var i = 0; i < agents.length; i++) {
        if (agents[i].charAt(0) === 'b') { hasBeacon = true; }
        if (agents[i].charAt(0) === 'o') { hasObsrver = true; }
        if (hasBeacon) { return 'red'; }
    }

    return 'blue';
}