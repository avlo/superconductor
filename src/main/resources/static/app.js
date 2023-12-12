let ws;

function connect() {
    ws = new WebSocket('ws://localhost:8080');
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

function createEnum(values) {
    const enumObject = {};
    for (const val of values) {
        enumObject[val] = val;
    }
    return Object.freeze(enumObject);
}

var Tag = createEnum(['E', 'A', 'P']);

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

function send() {
    const concat = [
        '0',
        $("#pubkey").val(),
        $("#created_at").val(),
        $("#kind").val(),
        $("#e_tag").val(),
        $("#p_tag").val(),
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
    let jsonstring = "["
        + "\"EVENT\","
        +
        JSON.stringify(
            {
                'id': id_hash,
                'kind': $("#kind").val(),
                'content': $("#content").val(),
                'pubkey': $("#pubkey").val(),
                'created_at': Date.now(),
                'tags': [['e', $("#e_tag").val()], ['p', $("#p_tag").val()]],
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        ) +
        "]";
    return jsonstring;
}

function sendContent(id_hash) {
    console.log("\nsending content...\n\n");
    let localjsonstring = replaceHash(id_hash);
    console.log(localjsonstring);
    console.log('\n\n');
    ws.send(localjsonstring);
}

function showEvent(content) {
    $("#events").append("<tr><td>" + content + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => send());
});

