function visualize() {
    simulation = getUrlVars();
    console.log(simulation);
}

function getUrlVars() {
    var vars = {}; 
    const startIndex = window.location.href.indexOf('?') + 1;
    const pairs = window.location.href.slice(startIndex).split('&');
    for(var i = 0; i < pairs.length; i++) {
        const pair = pairs[i].split('=');
        const key = pair[0]; const value = pair[1];
        vars[key] = value;
    }
    return vars;
}