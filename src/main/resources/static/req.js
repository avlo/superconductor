const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:5555'
});

stompClient.onConnect = (frame) => {
    setConnected(true);
    console.log('Connected: ' + frame);
    // stompClient.subscribe('/topic/topic_001', (event) => {
    stompClient.subscribe('/', function (message) {
        // called when the client receives a STOMP message from the server
        if (message) {
            console.log("1111111111111111111111111111");
            console.log(message);
            if (message.body) {
                console.log("2222222222222222222222222");
                console.log(message.body);
                console.log("3333333333333333333333");
                // showEvent(JSON.parse(message.body));
                console.log("4444444444444444444444");
                showEvent(JSON.parse(message.body).content);
                console.log("555555555555555555555555");
            } else {
                console.log("XXXXXXXXXXXXXXXXXXXXXXX");
                console.log("XXXXXXXXXXXXXXXXXXXXXXX");
            }
        }
    });
};

stompClient.onWebSocketError = (error) => {
    console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
    console.error('Broker reported error: ' + frame.headers['message']);
    console.error('Additional details: ' + frame.body);
};

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

function connect() {
    stompClient.activate();
}

function disconnect() {
    stompClient.deactivate();
    setConnected(false);
    console.log("Disconnected");
}

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
                'ids': [$("#content").val(), "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc1234", "432101ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc5678"],
                'authors': [$("#authors1").val(), $("#authors2").val()],
                'kinds': [1,23,3],
                '#e': [$("#referencedEvents").val(), "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc1234"],
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

    stompClient.publish({
        // destination: "/app/topic_001",
        destination: "/",
        body: localjsonstring
    });
}

function showEvent(content) {
    console.log("++++++++++++++++++++++++");
    console.log("REQ");
    console.log("++++++++++++++++++++++++");
    console.log("content: " + content);
    console.log("++++++++++++++++++++++++");

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