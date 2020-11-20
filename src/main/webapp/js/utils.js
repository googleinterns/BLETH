/**
 * Convert an object to a query string.
 * @param {Object} params is the converted object.
 * @returns {String} a query string.
 */
export function toQueryString(params) {
    const keyValuePairs = [];
    for (const key in params) {
      keyValuePairs.push(encodeURIComponent(key) + '=' + encodeURIComponent(params[key]));
    }
    return keyValuePairs.join('&');
}

/**
 * Get url variables as JSON.
 * @returns {Object} a JSON storing all variables from url.
 */
export function getUrlVars() {
    var vars = {}; 
    const startIndex = window.location.href.indexOf('?') + 1;
    const pairs = window.location.href.slice(startIndex).split('&');
    for(var i = 0; i < pairs.length; i++) {
        const pair = pairs[i].split('=');
        const key = pair[0]; 
        const value = pair[1];
        vars[key] = value;
    }
    return vars;
}

/**
 * JS sleep method implementation.
 * @param {Number} ms number of milliseconds to sleep.
 * @returns {Promise} a promise which resolves after ms milliseconds.
 */
export function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}