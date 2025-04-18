$(function () {
    $("#send57").click(() => createEvent(generate57TypeScriptEvent()));
});

function generate57TypeScriptEvent() {
    const tags = [
        ['subject', $("#subject").val()],
        ['relays', $("#relays").val()],
        ['amount', $("#amount").val()],
        ['lnurl', $("#lnurl").val()],
        ['e', $("#57-e_tag").val()],
        ['p', $("#57-p_tag").val()],
        ['t', $("#57-t_tag").val()],
        ['g', $("#57-g_tag").val()]
    ];

    let event = {
        id: '',
        kind: Number($("#57-kind").val()),
        created_at: Math.floor(Date.now() / 1000),
        content: $("#57-content").val(),
        tags: tags,
        pubkey: '',
        sig: ''
    }
    return event;
}
