$(function () {
    $("#send00").click(() => hashThenSend00());
});

function hashThenSend00() {
    const concat = [
        '0',
        $("#00-pubkey").val(),
        $("#00-kind").val()
    ].join(",");

    const text = [
        '[',
        concat,
        ']'
    ].join('');

    createDigest(text).then((hash) => sendContent00(hash));
}

// TODO: fix signature
function replaceHash00(id_hash) {
    return "["
        + "\"EVENT\","
        + JSON.stringify(
            {
                'id': id_hash,
                'kind': $("#00-kind").val(),
                'content': '{"name": "Michael", "about": "Software Engineer", "picture": "me.png", "nip05": "mikkthemagnificent@getalby.com", "other": [{"test_key": "test_value"}]}',
                'pubkey': $("#00-pubkey").val(),
                'created_at': Date.now(),
                'sig': '86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546'
            }
        )
        + "]";
}

function sendContent00(id_hash) {
    console.log("\nsending content...\n\n");
    console.log("sending w/ date now: " + dateNow);
    currentSubscriptonId = id_hash;
    let localjsonstring = replaceHash00(id_hash);
    console.log(localjsonstring);
    console.log('\n\n');
    ws.send(localjsonstring);
}
