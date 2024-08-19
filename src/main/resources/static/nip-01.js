function hashThenSend01() {
    const concat = [
        '0',
        $("#01-pubkey").val(),
        $("#01-kind").val(),
        $("#e_tag").val(),
        $("#p_tag").val(),
        $("#g_tag").val(),
        $("#content").val()
    ].join(",");

    const text = [
        '[',
        concat,
        ']'
    ].join('');

    createDigest(text).then((hash) => sendContent01(hash));
}

function replaceHash01(id_hash) {
    return "["
        + "\"EVENT\","
        + JSON.stringify(
            {
                'id': id_hash,
                'kind': $("#01-kind").val(),
                'content': $("#content").val(),
                'tags': [
                    ['e', $("#e_tag").val()],
                    ['p', $("#p_tag").val()],
                    ['g', $("#g_tag").val()]
                ],
                'pubkey': $("#01-pubkey").val(),
                'created_at': Date.now(),
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        )
        + "]";
}