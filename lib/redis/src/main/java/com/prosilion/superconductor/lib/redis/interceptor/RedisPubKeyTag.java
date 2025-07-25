package com.prosilion.superconductor.lib.redis.interceptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.Key;
import com.prosilion.nostr.tag.Tag;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Tag(code = "p")
@JsonPropertyOrder({"publicKey", "mainRelayUrl", "petName"})
public record RedisPubKeyTag(
    @Getter @Key String publicKey,
    @Getter @Nullable @Key @JsonInclude(JsonInclude.Include.NON_NULL) String mainRelayUrl,
    @Getter @Nullable @Key @JsonInclude(JsonInclude.Include.NON_NULL) String petName) implements BaseTag, RedisBaseTagIF {

  public RedisPubKeyTag(String publicKey) {
    this(publicKey, null);
  }

  public RedisPubKeyTag(String publicKey, String mainRelayUrl) {
    this(publicKey, mainRelayUrl, null);
  }
}
