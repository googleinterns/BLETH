import { toQueryString, getUrlVars, sleep } from './utils.js';

window.visualize = visualize; // Add function to global scope.

async function visualize() {
    var simulation = getUrlVars();
    await sleep(3000);
    console.log(simulation);
}
