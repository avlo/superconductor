let ws

function connect() {
  ws = new WebSocket("/");
  ws.onmessage = function (baseMessage) {
    showResponse(baseMessage.data);
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
  $("#sendGenericEvent").prop("disabled", !connected);
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

function createEvent(unsignedEventContent) {
  console.log("createEvent(unsignedEventContent): \n\n" + unsignedEventContent + "\n\n");
  signEvent(unsignedEventContent).then((fullyPopulatedSignedEvent) => sendContent(fullyPopulatedSignedEvent));
}

async function signEvent(event) {
  console.log('signEvent() input: \n\n' + event + '\n\n');
  var signedPopulatedEvent = await window.nostr.signEvent(event);
  console.log('signEvent() output: ' + signedPopulatedEvent);
  return signedPopulatedEvent;
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

function showResponse(baseMessageJson) {
  const genericEventRecord = parseGenericEventRecord(baseMessageJson);
  if (genericEventRecord != null) {
    $("#events").append("<tr><td>" + renderGenericEventRecord(genericEventRecord) + "</td></tr>");
    return;
  }

  let formattedJson = baseMessageJson;
  try {
    formattedJson = JSON.stringify(JSON.parse(baseMessageJson), null, 2);
  } catch (e) {
    formattedJson = JSON.stringify({message: baseMessageJson}, null, 2);
  }
  $("#events").append("<tr><td><pre>" +
      syntaxHighlight(formattedJson) +
      "</pre></td></tr>");
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

function parseGenericEventRecord(baseMessageJson) {
  try {
    return GenericEventRecord.fromJson(baseMessageJson);
  } catch (e) {
    return null;
  }
}

function renderGenericEventRecord(genericEventRecord) {
  const eventObject = genericEventRecord.toObject();
  const createdAtDisplay = eventObject.created_at != null
      ? new Date(eventObject.created_at * 1000).toISOString()
      : "";

  return "<div class=\"generic-event-card\">"
      + "<div class=\"generic-event-card-header\">EVENT · kind " + escapeHtml(String(eventObject.kind ?? "")) + "</div>"
      + "<dl class=\"generic-event-card-grid\">"
      + "<dt>Id</dt><dd class=\"generic-event-break\">" + escapeHtml(String(eventObject.id ?? "")) + "</dd>"
      + "<dt>Pubkey</dt><dd class=\"generic-event-break\">" + escapeHtml(String(eventObject.pubkey ?? "")) + "</dd>"
      + "<dt>Created At</dt><dd>" + escapeHtml(createdAtDisplay) + "</dd>"
      + "<dt>Tags</dt><dd class=\"generic-event-break\">" + escapeHtml(JSON.stringify(eventObject.tags ?? [])) + "</dd>"
      + "</dl>"
      + "<div class=\"generic-event-content-label\">Content</div>"
      + "<pre class=\"generic-event-content\">" + escapeHtml(String(eventObject.content ?? "")) + "</pre>"
      + "<details><summary>Raw JSON</summary><pre>"
      + syntaxHighlight(genericEventRecord.toPrettyJson())
      + "</pre></details>"
      + "</div>";
}

function escapeHtml(value) {
  return value
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
}
