function hashThenSend() {
    const concat = [
        '0',
        $("#pubkey").val(),
        $("#created_at").val(),
        $("#kind").val(),
        $("#subject").val(),
        $("#relays").val(),
        $("#amount").val(),
        $("#lnurl").val(),
        $("#content").val(),
        $("#location").val(),
        $("#g_tag").val(),
        $("#t_tag").val(),
        $("#number").val(),
        $("#currency").val(),
        $("#frequency").val(),
        $("#e_tag").val(),
        $("#p_tag").val(),
    ].join(",");

    const text = [
        '[',
        concat,
        ']'
    ].join('');

    createDigest(text).then((hash) => sendContent(hash));
}

function replaceHash(id_hash) {
    return "["
        + "\"EVENT\","
        + JSON.stringify(
            {
                'id': id_hash,
                'kind': $("#kind").val(),
                'content': $("#content").val(),
                'tags': [
                    ['subject', $("#subject").val()],
                    ['relays', $("#relays").val()],
                    ['amount', $("#amount").val()],
                    ['lnurl', $("#lnurl").val()],
                    ['e', $("#e_tag").val()],
                    ['p', $("#p_tag").val()],
                    ['t', $("#t_tag").val()],
                    ['g', $("#g_tag").val()]
                ],
                'pubkey': $("#pubkey").val(),
                'created_at': Date.now(),
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        )
        + "]";
}