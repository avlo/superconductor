$(function () {
    $("#send57").click(() => hashThenSend57());
});

function hashThenSend57() {
    const concat = [
        '0',
        $("#57-pubkey").val(),
        $("#created_at").val(),
        $("#57-kind").val(),
        $("#subject").val(),
        $("#relays").val(),
        $("#amount").val(),
        $("#lnurl").val(),
        $("#57-content").val(),
        $("#location").val(),
        $("#g_tag").val(),
        $("#t_tag").val(),
        $("#number").val(),
        $("#currency").val(),
        $("#frequency").val(),
        $("#57-e_tag").val(),
        $("#57-p_tag").val(),
    ].join(",");

    const text = [
        '[',
        concat,
        ']'
    ].join('');

    createDigest(text).then((hash) => sendContent57(hash));
}

function replaceHash57(id_hash) {
    return "["
        + "\"EVENT\","
        + JSON.stringify(
            {
                'id': id_hash,
                'kind': $("#57-kind").val(),
                'content': $("#57-content").val(),
                'tags': [
                    ['subject', $("#subject").val()],
                    ['relays', $("#relays").val()],
                    ['amount', $("#amount").val()],
                    ['lnurl', $("#lnurl").val()],
                    ['e', $("#57-e_tag").val()],
                    ['p', $("#57-p_tag").val()],
                    ['t', $("#t_tag").val()],
                    ['g', $("#g_tag").val()]
                ],
                'pubkey': $("#57-pubkey").val(),
                'created_at': Date.now(),
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        )
        + "]";
}

function sendContent57(id_hash) {
    console.log("\nsending content...\n\n");
    console.log("sending w/ date now: " + dateNow);
    currentSubscriptonId = id_hash;
    let localjsonstring = replaceHash57(id_hash);
    console.log(localjsonstring);
    console.log('\n\n');
    ws.send(localjsonstring);
}