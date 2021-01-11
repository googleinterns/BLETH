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