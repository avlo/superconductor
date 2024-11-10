$(function () {
    $("#send01").click(() => createEvent(generate01TypeScriptEvent()));
});

function generate01TypeScriptEvent() {
    const tags = [
        ['e', $("#01-e_tag").val()],
        ['p', $("#01-p_tag").val()],
        ['g', $("#01-g_tag").val()]
    ];

    let event = {
        id: '',
        kind: Number($("#01-kind").val()),
        created_at: Math.floor(Date.now() / 1000),
        content: $("#01-content").val(),
        tags: tags,
        pubkey: '',
        sig: ''
    }
    return event;
}
