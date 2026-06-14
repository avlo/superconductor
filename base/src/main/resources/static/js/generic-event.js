let tagRowCount = 0;

const TAG_DEFINITIONS = {
  a: [
    {field: "kind", type: "number"},
    {field: "publicKey", type: "text"},
    {field: "uuid", type: "text"},
    {field: "relayUrl", type: "text"}
  ],
  p: [
    {field: "publicKey", type: "text"},
    {field: "relayUrl", type: "text"}
  ],
  d: [
    {field: "uuid", type: "text"}
  ]
};

$(function () {
  $("#send-generic").click(() => createEvent(gatherUnsignedEventContent()));
  $("#add-tag").click(() => addTagRow());
});

function addTagRow() {
  const rowId = "tag-row-" + (tagRowCount++);
  const options = Object.keys(TAG_DEFINITIONS)
      .map((code) => '<option value="' + code + '">' + code + '</option>')
      .join("");
  $("#tag-rows").append(
    '<tr id="' + rowId + '">' +
      '<td><select class="form-control tag-key">' + options + '</select></td>' +
      '<td class="tag-values"></td>' +
      '<td><button type="button" class="btn btn-default btn-sm" ' +
          'onclick="$(\'#' + rowId + '\').remove()">Remove</button></td>' +
    '</tr>'
  );

  const $row = $("#" + rowId);
  $row.find(".tag-key").on("change", function () {
    renderTagValueInputs($row, $(this).val());
  });
  renderTagValueInputs($row, $row.find(".tag-key").val());
}

function renderTagValueInputs($row, code) {
  const $cell = $row.find(".tag-values");
  $cell.empty();
  (TAG_DEFINITIONS[code] || []).forEach(({field, type}) =>
    $cell.append(
      '<input type="' + type + '" class="form-control tag-value" data-field="' + field + '" placeholder="' + field + '">'
    )
  );
}

function getTags() {
  const tags = [];
  $("#tag-rows tr").each(function () {
    const key = $(this).find(".tag-key").val();
    const values = $(this).find(".tag-value").map(function () {
      return $(this).val();
    }).get();
    if (key) {
      if (key === "a") {
        const [kind, publicKey, uuid, relayUrl] = values;
        tags.push(["a", kind + ":" + publicKey + ":" + uuid, relayUrl]);
      } else {
        tags.push([key, ...values]);
      }
    }
  });
  return tags;
}

function gatherUnsignedEventContent() {
  return {
    id: '',
    kind: Number($("#ger-kind").val()),
    created_at: Math.floor(Date.now() / 1000),
    content: $("#ger-content").val(),
    tags: getTags(),
    pubkey: '',
    sig: ''
  };
}
