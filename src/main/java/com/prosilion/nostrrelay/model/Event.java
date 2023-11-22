package com.prosilion.nostrrelay.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record Event(
    String id,
    String pubkey,
    @JsonProperty("created_at") long createdAt,
    int kind,
    List<Tag> tags,
    String content,
    String sig
) {}