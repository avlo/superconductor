$(function () {
    $("#send2112").click(() => createEvent(generate2112TypeScriptEvent()));
});

function generate2112TypeScriptEvent() {
    let voteSelection = $("#2112-content").val();
    
    const tags = [
        ['a', '30009:2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984:' + voteSelection],
    ];
    
    let voteIntegerAsString = voteSelection === 'UPVOTE' ? '1' : '-1'; 

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
