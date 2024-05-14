function hashThenSend() {
    const concat = [
        '0',
        $("#pubkey").val(),
        dateNow,
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
    let clickNow = Date.now();
    return "["
        + "\"REQ\","
        + "\"" + id_hash + "\","
        + JSON.stringify(
            {
                // 'ids': [$("#content").val(), "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc1234", "432101ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc5678"]
                'ids': [$("#idcontent").val()],
                // 'authors': [$("#authors1").val(), $("#authors2").val()],
                'authors': [$("#authors1").val()],
                // 'kinds': [1,23,3],
                // '#e': [$("#referencedEvents").val(), "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc1234"],
                // '#p': [$("#referencePubKeys").val()],
                // 'since': clickNow-1000,
                // 'until': clickNow,
                // 'limit': '1'
            }
        )
        + "]";
}