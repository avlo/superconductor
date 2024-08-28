$(function () {
    // $("#sendrequest").click(() => hashThenSendRequest());
    $("#sendrequest").click(() => sendContentRequest($("#subscription_id").val()));
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

function populateRequestJson(subscription_id) {
    return "["
        + "\"REQ\","
        + "\"" + subscription_id + "\","
        + stringifyJson()
        + "]";
}

function sendContentRequest(subscription_id) {
    console.log("\nsending content...\n\n");
    console.log("sending w/ date now: " + dateNow);
    currentSubscriptonId = subscription_id;
    let outboundJson = populateRequestJson(subscription_id);
    console.log(outboundJson);
    console.log('\n\n');
    ws.send(outboundJson);
}