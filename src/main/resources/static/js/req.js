$(function () {
    $("#sendrequest").click(() => hashThenSendRequest());
});

function hashThenSendRequest() {
    const concat = [
        '0',
        $("#pubkey").val(),
        dateNow,
        // $("#kind").val(),
        // $("#e_tag").val(),
        // $("#p_tag").val(),
        $("#idcontent").val()
    ].join(",");

    const text = [
        '[',
        concat,
        ']'
    ].join('');

    createDigest(text).then((hash) => sendContentRequest(hash));
}

function cullEmptyKeyValuePairs() {
    return {
        'ids': (($("#idcontent").val() !== "") ? [$("#idcontent").val()] : undefined),
        'authors': (($("#authors1").val() !== "") ? [$("#authors1").val()] : undefined)
        // 'kinds': [1,23,3],
        // '#e': [$("#referencedEvents").val(), "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc1234"],
        // '#p': [$("#referencePubKeys").val()],
        // 'since': clickNow-1000,
        // 'until': clickNow,
        // 'limit': '1'
    }
}

function stringifyJson() {
    return JSON.stringify(cullEmptyKeyValuePairs());
}

function replaceHashRequest(id_hash) {
    return "["
        + "\"REQ\","
        + "\"" + id_hash + "\","
        + stringifyJson()
        + "]";
}

function sendContentRequest(id_hash) {
    console.log("\nsending content...\n\n");
    console.log("sending w/ date now: " + dateNow);
    currentSubscriptonId = id_hash;
    let localjsonstring = replaceHashRequest(id_hash);
    console.log(localjsonstring);
    console.log('\n\n');
    ws.send(localjsonstring);
}