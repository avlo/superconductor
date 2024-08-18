function hashThenSend() {
    const concat = [
        '0',
        $("#pubkey").val(),
        22242,
        $("#relay").val(),
        $("#challenge").val()
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
        + "\"AUTH\","
        + JSON.stringify(
            {
                'id': id_hash,
                'pubkey': $("#pubkey").val(),
                'created_at': Date.now(),
                'kind': 22242,
                'tags': [
                    ['relay', $("#relay").val()],
                    ['challenge', $("#challenge").val()]
                ],
                'content': '',
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        )
        + "]";
}