$(function () {
    $("#send01").click(() => createEvent());
});

function createEvent() {
    var event = getStringifiedJson();
    // var event = getConstJson();
    // var event = getObjectJson();
    console.log("createEvent() created event JSON: \n\n" + event + "\n\n");
    signEvent(event).then((fullyPopulatedSignedEvent) => sendContent01(fullyPopulatedSignedEvent));
}

// function Event(created_at, kind, tags, content) {
//     this.created_at = created_at;
//     this.kind = kind;
//     this.tags = tags;
//     this.content = content;
// }

// function getObjectJson() {
//     return new Event(
//         dateNow,
//         Number($("#01-kind").val()),
//         [
//             ['e', $("#e_tag").val()],
//             ['p', $("#p_tag").val()],
//             ['g', $("#01-g_tag").val()],
//         ],
//         $("#01-content").val()
//     );
// }

function getStringifiedJson() {
    var text =
        JSON.stringify(
            {
                'created_at': dateNow,
                'kind': Number($("#01-kind").val()),
                'tags': [
                    ['e', $("#e_tag").val()],
                    ['p', $("#p_tag").val()],
                    ['g', $("#01-g_tag").val()],
                ],
                'content': $("#01-content").val()
            }
        );
    // var encoded = encodeURIComponent(text);
    // console.log("encoded: " + encoded);
    return text;
}

function getConstJson() {
    const event = {
        created_at: dateNow,
        kind: Number($("#01-kind").val()),
        tags: [
            ['e', $("#e_tag").val()],
            ['p', $("#p_tag").val()],
            ['g', $("#01-g_tag").val()],
        ],
        content: $("#01-content").val()
    }
    return event;
}

function saveEventId(signedPopulatedEvent) {
    var eventId = JSON.parse(signedPopulatedEvent, "peer");
    console.log("saved event id: " + eventId);
    currentSubscriptonId = eventId;
}

function sendContent01(signedPopulatedEvent) {
    console.log("\nsending content...\n\n");
    saveEventId(signedPopulatedEvent);
    console.log("sending event JSON: " + dateNow);
    console.log(signedPopulatedEvent);
    console.log('\n\n');
    // ws.send(localjsonstring);
}