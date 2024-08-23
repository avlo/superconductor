$(function () {
    $("#send99").click(() => hashThenSend99());
});

function hashThenSend99() {
    const concat = [
        '0',
        $("#99-pubkey").val(),
        $("#created_at").val(),
        $("#99-kind").val(),
        $("#99-subject").val(),
        $("#title").val(),
        $("#summary").val(),
        $("#content").val(),
        $("#99-location").val(),
        $("#99-g_tag").val(),
        $("#99-t_tag").val(),
        $("#99-number").val(),
        $("#99-currency").val(),
        $("#99-frequency").val(),
        $("#99-e_tag").val(),
        $("#99-p_tag").val(),
    ].join(",");

    const text = [
        '[',
        concat,
        ']'
    ].join('');

    createDigest(text).then((hash) => sendContent99(hash));
}

function replaceHash99(id_hash) {
    return "["
        + "\"EVENT\","
        + JSON.stringify(
            {
                'id': id_hash,
                'kind': $("#99-kind").val(),
                'content': $("#content").val(),
                'tags': [
                    ['subject', $("#99-subject").val()],
                    ['title', $("#title").val()],
                    ['published_at', Date.now()],
                    ['summary', $("#summary").val()],
                    ['location', $("#99-location").val()],
                    ['price', $("#99-number").val(), $("#99-currency").val(), $("#99-frequency").val()],
                    ['e', $("#99-e_tag").val()],
                    ['p', $("#99-p_tag").val()],
                    ['t', $("#99-t_tag").val()],
                    ['g', $("#99-g_tag").val()]
                ],
                'pubkey': $("#99-pubkey").val(),
                'created_at': Date.now(),
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        )
        + "]";
}

function sendContent99(id_hash) {
    console.log("\nsending content...\n\n");
    console.log("sending w/ date now: " + dateNow);
    currentSubscriptonId = id_hash;
    let localjsonstring = replaceHash99(id_hash);
    console.log(localjsonstring);
    console.log('\n\n');
    ws.send(localjsonstring);
}