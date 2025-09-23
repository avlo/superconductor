$(function () {
    $("#send2112").click(() => createEvent(generate2112TypeScriptEvent()));
});

function generate2112TypeScriptEvent() {
    let voteSelection = $("#2112-content").val();
    
    const tags = [
        ['a', '30009:e04e1c1c30df6058433f61681644fd24914f2e02e420496086c61f53eb504c04:' + voteSelection],
        ['p', $("#2112-p_tag").val()]
    ];
    
    let voteIntegerAsString = voteSelection === 'UNIT_UPVOTE' ? '1' : '-1'; 

    let event = {
        id: '',
        kind: Number($("#2112-kind").val()),
        created_at: Math.floor(Date.now() / 1000),
        content: voteIntegerAsString,
        tags: tags,
        pubkey: '',
        sig: ''
    }
    
    return event;
}
