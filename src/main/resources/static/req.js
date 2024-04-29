function hashThenSend() {
    const concat = [
        '0',
        // $("#pubkey").val(),
        $("#created_at").val(),
        // $("#kind").val(),
        // $("#e_tag").val(),
        // $("#p_tag").val(),
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
        + "\"REQ\","
        + "\"" + id_hash + "\","
        + JSON.stringify(
            {
                'ids': [$("#content").val(), "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc1234", "432101ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc5678"],
                'authors': [$("#authors1").val(), $("#authors2").val()],
                'kinds': [1,23,3],
                '#e': [$("#referencedEvents").val(), "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc1234"],
                '#p': [$("#referencePubKeys").val()],
                'since': '1712006760',
                'until': '1712006761',
                'limit': '1'
            }
        )
        + "]";
}