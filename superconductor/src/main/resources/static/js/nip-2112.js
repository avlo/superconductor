$(function () {
    $("#send2112").click(() => createEvent(generate2112TypeScriptEvent()));
});

function generate2112TypeScriptEvent() {
    const tags = [
        ['v', $("#2112-v_tag").val()]
    ];

    let event = {
        id: '',
        kind: Number($("#2112-kind").val()),
        created_at: Math.floor(Date.now() / 1000),
        content: $("#2112-content").val(),
        tags: tags,
        pubkey: '',
        sig: ''
    }
    return event;
}
