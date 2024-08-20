let ws
let currentSubscriptonId
let dateNow

function connect() {
    ws = new WebSocket('ws://localhost:5555');
    ws.onmessage = function (messageEvent) {
        showEvent(messageEvent.data);
    }
    setConnected(true);
    dateNow = Date.now();
    console.log("date now: " + dateNow);
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    $("#reqclose").prop("disabled", !connected);

    $("#sendregister").prop("disabled", !connected);
    $("#send00").prop("disabled", !connected);
    $("#send01").prop("disabled", !connected);
    $("#send57").prop("disabled", !connected);
    $("#send99").prop("disabled", !connected);

    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#events").html("");
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Disconnected via WS close");
}

async function createDigest(message) {
    const utf8 = new Uint8Array(message.length);
    new TextEncoder().encodeInto(message, utf8);
    const hashBuffer = await window.crypto.subtle.digest("SHA-256", utf8);
    const hashArray = Array.from(new Uint8Array(hashBuffer)); // convert buffer to byte array
    return hashArray
        .map(b => b
            .toString(16)
            .padStart(2, "0"))
        .join(""); // convert bytes to hex string
}

function sendClose() {
    let localjsonstring = replaceCloseHash(currentSubscriptonId);
    console.log(localjsonstring);
    console.log('\n\n');
    ws.send(localjsonstring);
    disconnect();
    setConnected(false);
    console.log("Disconnected via Nostr CLOSE");
}

function replaceCloseHash(id_hash) {
    return "["
        + "\"CLOSE\","
        + "\"" + id_hash + "\""
        + "]";
}

function showEvent(content) {
    let jsonPretty = JSON.stringify(JSON.parse(content), null, 2);
    $("#events").append("<tr><td><pre>" + syntaxHighlight(jsonPretty) + "</pre></td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#reqclose").click(() => sendClose());
});

function syntaxHighlight(json) {
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        var cls = 'number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'key';
            } else {
                cls = 'string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'boolean';
        } else if (/null/.test(match)) {
            cls = 'null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}












