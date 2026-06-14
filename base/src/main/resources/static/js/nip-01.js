$(function () {
    $("#send01").click(() => createEvent(gatherUnsignedEventConent()));
});

function getTags() {
    const tags = [
        ['e', $("#01-e_tag").val()],
        ['p', $("#01-p_tag").val()],
        ['g', $("#01-g_tag").val()]
    ];
    return tags;
}

function gatherUnsignedEventConent() {
    let event = {
        id: '',
        kind: Number($("#01-kind").val()),
        created_at: Math.floor(Date.now() / 1000),
        content: $("#01-content").val(),
        tags: getTags(),
        pubkey: '',
        sig: ''
    }
    return event;
}
