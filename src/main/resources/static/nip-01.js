function hashThenSend() {
    const concat = [
        '0',
        $("#pubkey").val(),
        // TODO: re-add below as fixed value exclusively used for testing
        $("#created_at").val(),
        // '1712006760',
        $("#kind").val(),
        $("#e_tag").val(),
        $("#p_tag").val(),
        $("#content").val()
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
                'pubkey': $("#pubkey").val(),
                'created_at': Date.now(),
                // TODO: hardcoding value exclusively used for testing
                // 'created_at': '1712006760',
                'tags': [
                    ['e', $("#e_tag").val()],
                    ['p', $("#p_tag").val()]
                ],
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        )
        + "]";
}