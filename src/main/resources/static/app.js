let ws;

function connect() {
    ws = new WebSocket('ws://localhost:8080/name');
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

// function sendName() {
//     ws.send($("#name").val());
// }

function showGreeting(message) {
    showEvent(message);
    console.log(message);
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
        $("#content").val()
    ].join(",");

    const text = [
        '[',
        concat,
        ']'
    ].join('');

    createDigest(text).then((hash) => sendContent(hash));
}

function sendContent() {
    console.log("111111111111111");
    ws.send(JSON.stringify(
        {
            'name': $("#name").val()
        })
    );

    // stompClient.publish({
    //     // destination: "/app/topic_001",
    //     destination: "/",
    //     body: JSON.stringify(
    //         {
    //             'id': id_hash,
    //             'pubkey': $("#pubkey").val(),
    //             'created_at': $("#created_at").val(),
    //             'kind': $("#kind").val(),
    //             'tags': [$("#e_tag").val(), $("#a_tag").val()],
    //             'sig': "SIG_XXX",
    //             'content': $("#content").val()
    //         }
    //     )
    // });
}

function showEvent(content) {
    $("#events").append("<tr><td>" + content + "</td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
    $("#send").click(() => sendContent());
});

