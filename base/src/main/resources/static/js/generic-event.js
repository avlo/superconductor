let tagRowCount = 0;

$(function () {
  $("#send-generic").click(() => createEvent(gatherUnsignedEventContent()));
  $("#add-tag").click(() => addTagRow());
});

function addTagRow() {
  const rowId = "tag-row-" + (tagRowCount++);
  $("#tag-rows").append(
    '<tr id="' + rowId + '">' +
      '<td><input type="text" class="form-control tag-key" placeholder="key"></td>' +
      '<td><input type="text" class="form-control tag-value" placeholder="value"></td>' +
      '<td><button type="button" class="btn btn-default btn-sm" ' +
          'onclick="$(\'#' + rowId + '\').remove()">Remove</button></td>' +
    '</tr>'
  );
}

function getTags() {
  const tags = [];
  $("#tag-rows tr").each(function () {
    const key = $(this).find(".tag-key").val();
    const val = $(this).find(".tag-value").val();
    if (key) {
      tags.push([key, val]);
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
