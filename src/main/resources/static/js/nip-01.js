$(function () {
    $("#send01").click(() => hashThenSend01());
});

function hashThenSend01() {
    const concat = [
        '0',
        pubKey,
        $("#01-kind").val(),
        $("#e_tag").val(),
        $("#p_tag").val(),
        $("#01-g_tag").val(),
        $("#01-content").val()
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
                'content': $("#01-content").val(),
                'tags': [
                    ['e', $("#e_tag").val()],
                    ['p', $("#p_tag").val()],
                    ['g', $("#01-g_tag").val()]
                ],
                'pubkey': pubKey,
                'created_at': Date.now(),
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        )
        + "]";
}

function sendContent01(id_hash) {
    console.log("\nsending content...\n\n");
    console.log("sending w/ date now: " + dateNow);
    currentSubscriptonId = id_hash;
    let localjsonstring = replaceHash01(id_hash);
    console.log(localjsonstring);
    console.log('\n\n');
    ws.send(localjsonstring);
}