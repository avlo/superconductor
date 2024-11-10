$(function () {
    $("#send01").click(() => createEvent());
});

function createEvent() {
    var event = getAsTypeScriptEvent();
    console.log("createEvent() created event JSON: \n\n" + event + "\n\n");
    signEvent(event).then((fullyPopulatedSignedEvent) => sendContent01(fullyPopulatedSignedEvent));
}

function getAsTypeScriptEvent() {
    let event = {
        id: '',
        kind: 1,
        created_at: Math.floor(Date.now() / 1000),
        content: 'hello world',
        tags: [],
        pubkey: '',
        sig: ''
    }
    return event;
}

function getAsEvent() {
    const tags = [
        '[\"e\",\"' + $("#e_tag").val() + '\"]',
        '[\"p\",\"' + $("#p_tag").val() + '\"]',
        '[\"g\",\"' + $("#01-g_tag").val() + '\"]'
    ].join(",");

    const inner = [
        '"id":""',
        '"pubkey":""',
        '"sig":""',
        '"created_at":' + dateNow,
        '"kind":' + Number($("#01-kind").val()),
        '"tags":['+ tags + ']',
        '"content":"' + $("#01-content").val() + '"'
    ].join(",");

    const outer = [
        '{' + inner + '}'
    ]
    return outer;
}

function getStringifiedJson() {
    const tags = [
        '[\"e\",\"' + $("#e_tag").val() + '\"]',
        '[\"p\",\"' + $("#p_tag").val() + '\"]',
        '[\"g\",\"' + $("#01-g_tag").val() + '\"]'
    ].join(",");

    const inner = [
        '"created_at":' + dateNow,
        '"kind":' + Number($("#01-kind").val()),
        '"tags":['+ tags + ']',
        '"content":"' + $("#01-content").val() + '"'
    ].join(",");

    const outer = [
        '{"event\":{' + inner + '}}'
    ]
    // var encoded = encodeURIComponent(text);
    // console.log("encoded: " + encoded);
    return outer;
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
