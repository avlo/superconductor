$(function () {
    $("#send99").click(() => createEvent(generate99TypeScriptEvent()));
});

function generate99TypeScriptEvent() {
    const dateNow = Math.floor(Date.now() / 1000);
    const tags = [
        ['subject', $("#99-subject").val()],
        ['title', $("#title").val()],
        ['published_at', dateNow],
        ['summary', $("#summary").val()],
        ['location', $("#99-location").val()],
        ['price', $("#99-number").val(), $("#99-currency").val(), $("#99-frequency").val()],
        ['e', $("#99-e_tag").val()],
        ['p', $("#99-p_tag").val()],
        ['t', $("#99-t_tag").val()],
        ['g', $("#99-g_tag").val()]
    ];

    let event = {
        id: '',
        kind: Number($("#99-kind").val()),
        created_at: dateNow,
        content: $("#content").val(),
        tags: tags,
        pubkey: '',
        sig: ''
    }
    return event;
}
