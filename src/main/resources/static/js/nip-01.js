$(function () {
    $("#send01").click(() => createEvent());
});

function createEvent() {
    var typeScriptEvent = generateTypeScriptEvent();
    console.log("createEvent() created event JSON: \n\n" + typeScriptEvent + "\n\n");
    signEvent(typeScriptEvent).then((fullyPopulatedSignedEvent) => sendContent01(fullyPopulatedSignedEvent));
}

function generateTypeScriptEvent() {
    const tags = [
        ['e', $("#e_tag").val()],
        ['p', $("#p_tag").val()],
        ['g', $("#01-g_tag").val()]
    ];

    let event = {
        id: '',
        kind: Number($("#01-kind").val()),
        created_at: dateNow,
        content: $("#01-content").val(),
        tags: tags,
        pubkey: '',
        sig: ''
    }
    return event;
}

function sendContent01(signedPopulatedEvent) {
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
