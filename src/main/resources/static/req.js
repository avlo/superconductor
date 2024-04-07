let ws = new WebSocket('ws://localhost:5555');

function connect() {
    ws.onmessage = function (data) {
        showGreeting(data.data);
    }
    setConnected(true);
}

function disconnect() {
    if (ws != null) {
        ws.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function showGreeting(message) {
    showEvent(message);
    console.log("console log" + message);
}

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#events").html("");
}

// function createEnum(values) {
//     const enumObject = {};
//     for (const val of values) {
//         enumObject[val] = val;
//     }
//     return Object.freeze(enumObject);
// }
//
// var Tag = createEnum(['E', 'A', 'P']);

async function createDigest(message) {
    const utf8 = new Uint8Array(message.length);
    new TextEncoder().encodeInto(message, utf8);
    const hashBuffer = await window.crypto.subtle.digest("SHA-256", utf8);

    // TODO: possibly make fxntional
    // return hashBuffer.split("")
    //     .map(c =>
    //         c.charCodeAt(0)
    //             .toString(16)
    //             .padStart(2, "0"))
    //     .join("");

    const hashArray = Array.from(new Uint8Array(hashBuffer)); // convert buffer to byte array
    return hashArray
        .map(b => b
            .toString(16)
            .padStart(2, "0"))
        .join(""); // convert bytes to hex string
}

function hashThenSend() {
    const concat = [
        '0',
        // $("#pubkey").val(),
        $("#created_at").val(),
        // $("#kind").val(),
        // $("#e_tag").val(),
        // $("#p_tag").val(),
        $("#content").val()
    ].join(",");

    const text = [
        '[',
        concat,
        ']'
    ].join('');

    createDigest(text).then((hash) => sendContent(hash));
}

function replaceHash(id_hash) {
    return "["
        + "\"REQ\","
        + "\"" + id_hash + "\","
        + JSON.stringify(
            {
                'ids': [$("#content").val()],
                'authors': [$("#authors").val()],
                'kinds': '1',
                '#e': [$("#referencedEvents").val()],
                '#p': [$("#referencePubKeys").val()],
                'since': '1712006760',
                'until': '1712006761',
                'limit': '1'
            }
        )
        + "]";
}

function sendContent(id_hash) {
    console.log("\nsending content...\n\n");
    let localjsonstring = replaceHash(id_hash);
    console.log(localjsonstring);
    console.log('\n\n');
    ws.send(localjsonstring);
}

function showEvent(content) {
    let jsonPretty = JSON.stringify(JSON.parse(content), null, 2);
    $("#events").append("<tr><td><pre>" + syntaxHighlight(jsonPretty) + "</pre></td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => hashThenSend());
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