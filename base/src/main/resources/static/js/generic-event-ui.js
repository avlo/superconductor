$(function () {
  $("#sendGenericEvent").click(() => createAndSaveGenericEventRecord());
});

function createAndSaveGenericEventRecord() {
  const unsignedEventContent = gatherUnsignedGenericEventConent();
  createEvent(unsignedEventContent);
}

function gatherUnsignedGenericEventConent() {
  return {
    id: '',
    kind: Number($("#generic-kind").val()),
    created_at: Math.floor(Date.now() / 1000),
    content: $("#generic-content").val(),
    tags: gatherGenericTags(),
    pubkey: '',
    sig: ''
  };
}

function gatherGenericTags() {
  const tags = [];
  appendTag(tags, "e", $("#generic-e_tag").val());
  appendTag(tags, "p", $("#generic-p_tag").val());
  appendTag(tags, "g", $("#generic-g_tag").val());

  const customTagsContent = ($("#generic-custom-tags").val() ?? "").trim();
  if (customTagsContent.length > 0) {
    const customTagLines = customTagsContent.split("\n");
    customTagLines.forEach((line) => {
      const lineWithoutWhitespace = line.trim();
      if (lineWithoutWhitespace.length === 0) {
        return;
      }
      const values = lineWithoutWhitespace.split(",").map((value) => value.trim()).filter((value) => value.length > 0);
      if (values.length > 0) {
        tags.push(values);
      }
    });
  }

  return tags;
}

function appendTag(tags, key, value) {
  if (value != null && String(value).trim().length > 0) {
    tags.push([key, String(value).trim()]);
  }
}
