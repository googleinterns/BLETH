function fetchHardCodedBoardState() {
    fetch('/hard-coded-board-state')
    .then(response => response.json())
    .then(boardState => console.log(boardState));
}
