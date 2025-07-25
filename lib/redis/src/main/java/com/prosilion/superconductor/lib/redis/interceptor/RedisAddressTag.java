package com.prosilion.superconductor.lib.redis.interceptor;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.prosilion.nostr.codec.serializer.AddressTagSerializer;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.Key;
import com.prosilion.nostr.tag.Tag;
import com.prosilion.nostr.user.PublicKey;
import lombok.Getter;
import org.springframework.lang.Nullable;

@Tag(code = "a")
@JsonPropertyOrder({"kind", "publicKey", "identifierTag", "relay"})
@JsonSerialize(using = AddressTagSerializer.class)
public record RedisAddressTag(
    @Getter @Key Kind kind,
    @Getter @Key String publicKey,
    @Getter @Nullable @Key @JsonInclude(JsonInclude.Include.NON_NULL) IdentifierTag identifierTag,
    @Getter @Nullable @Key @JsonInclude(JsonInclude.Include.NON_NULL) Relay relay) implements BaseTag, RedisBaseTagIF {

  public RedisAddressTag(Kind kind, String publicKey) {
    this(kind, publicKey, null);
  }

  public RedisAddressTag(Kind kind, String publicKey, IdentifierTag identifierTag) {
    this(kind, publicKey, identifierTag, null);
  }

  public RedisAddressTag(Kind kind, String publicKey, IdentifierTag identifierTag, Relay relay) {
    this.kind = kind;
    this.publicKey = publicKey;
    this.identifierTag = identifierTag;
    this.relay = relay;
  }

  //  TODO: remove below two methods, unused  
  @Override
  public BaseTag intercept(BaseTag baseTag) {
    AddressTag addressTag = (AddressTag) baseTag;
    return new RedisAddressTag(addressTag.getKind(), addressTag.publicKey().toString(), addressTag.identifierTag(), addressTag.getRelay());
  }

  @Override
  public BaseTag revert(BaseTag baseTag) {
    RedisAddressTag redisAddressTag = (RedisAddressTag) baseTag;
    return new AddressTag(
        redisAddressTag.getKind(),
        new PublicKey(redisAddressTag.getPublicKey()),
        redisAddressTag.getIdentifierTag(),
        redisAddressTag.getRelay());
  }

  @Override
  public String getCode() {
    return BaseTag.super.getCode();
  }
}
