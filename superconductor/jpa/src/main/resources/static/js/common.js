let ws

function connect() {
    ws = new WebSocket("/");
    ws.onmessage = function (messageEvent) {
        showEvent(messageEvent.data);
    }
    setConnected(true);
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
    $("#send2112").prop("disabled", !connected);
    $("#sendrequest").prop("disabled", !connected);

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

function createEvent(typeScriptEvent) {
    console.log("createEvent() created event JSON: \n\n" + typeScriptEvent + "\n\n");
    signEvent(typeScriptEvent).then((fullyPopulatedSignedEvent) => sendContent(fullyPopulatedSignedEvent));
}

async function signEvent(event) {
    console.log('signEvent() input: \n\n' + event + '\n\n');
    var signedPopulatedEvent = await window.nostr.signEvent(event);
    console.log('signEvent() output: ' + signedPopulatedEvent);
    return signedPopulatedEvent;
}

function sendContent(signedPopulatedEvent) {
    console.log("\nsending content...\n\n");
    console.log(addEventMessageWrapper(signedPopulatedEvent));
    console.log('\n\n');
    ws.send(addEventMessageWrapper(signedPopulatedEvent));
}

function addEventMessageWrapper(id_hash) {
    return "["
        + "\"EVENT\","
        + JSON.stringify(id_hash
        )
        + "]";
}

function showEvent(content) {
    var jsonPretty = JSON.stringify(JSON.parse(content), null, 2);
    $("#events").append("<tr><td><pre>" + syntaxHighlight(jsonPretty) + "</pre></td></tr>");
}

$(function () {
    $("form").on('submit', (e) => e.preventDefault());
    $("#connect").click(() => connect());
    $("#disconnect").click(() => disconnect());
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
