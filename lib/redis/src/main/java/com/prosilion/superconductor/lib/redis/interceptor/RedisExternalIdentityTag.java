package com.prosilion.superconductor.lib.redis.interceptor;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.prosilion.nostr.codec.serializer.ExternalIdentityTagSerializer;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.Key;
import com.prosilion.nostr.tag.Tag;
import lombok.Getter;

@Tag(code = "i")
@JsonPropertyOrder({"kind", "identifierTag", "formula"})
@JsonSerialize(using = ExternalIdentityTagSerializer.class)
public record RedisExternalIdentityTag(
    @Getter @Key Kind kind,
    @Getter @Key IdentifierTag identifierTag,
    @Getter @Key String formula) implements BaseTag, RedisBaseTagIF {
}
