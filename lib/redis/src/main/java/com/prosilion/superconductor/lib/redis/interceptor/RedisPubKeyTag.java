package com.prosilion.superconductor.lib.redis.interceptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.Key;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.Tag;
import com.prosilion.nostr.user.PublicKey;
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

//  TODO: remove below two methods, unused
  @Override
  public BaseTag intercept(BaseTag baseTag) {
    PubKeyTag pubKeyTag = (PubKeyTag) baseTag;
    return new RedisPubKeyTag(
        pubKeyTag.getPublicKey().toString(),
        pubKeyTag.getMainRelayUrl());
  }

  @Override
  public BaseTag revert(BaseTag baseTag) {
    RedisPubKeyTag redisPubKeyTag = (RedisPubKeyTag) baseTag;
    return new PubKeyTag(
        new PublicKey(
            redisPubKeyTag.getPublicKey()),
        redisPubKeyTag.getMainRelayUrl(),
        redisPubKeyTag.getPetName());
  }

  @Override
  public String getCode() {
    return BaseTag.super.getCode();
  }
}
