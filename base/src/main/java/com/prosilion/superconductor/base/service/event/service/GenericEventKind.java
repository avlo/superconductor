package com.prosilion.superconductor.base.service.event.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.crypto.HexStringValidator;
import com.prosilion.nostr.crypto.bech32.Bech32;
import com.prosilion.nostr.crypto.bech32.Bech32Prefix;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import java.beans.Transient;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public record GenericEventKind(
    @Getter @JsonProperty("id") String eventId,
    @Getter @JsonProperty("pubkey") PublicKey publicKey,
    @Getter @JsonProperty("created_at") @EqualsAndHashCode.Exclude Long createdAt,
    @Getter @EqualsAndHashCode.Exclude Kind kind,
    @Getter @EqualsAndHashCode.Exclude @JsonProperty("tags") List<BaseTag> tags,
    @Getter @EqualsAndHashCode.Exclude String content,
    @Getter @JsonProperty("sig") @EqualsAndHashCode.Exclude Signature signature) implements GenericEventKindIF {

  public GenericEventKind {
    HexStringValidator.validateHex(eventId, 64);
  }

  @Override
  public String toBech32() {
//    TODO: cleanup below
    try {
      return Bech32.toBech32(Bech32Prefix.NOTE, this.getEventId());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Transient
  @Override
  public Supplier<ByteBuffer> getByteArraySupplier() throws NostrException {
    byte[] serializedEvent = serialize().getBytes(StandardCharsets.UTF_8);
    log.debug(String.format("Serialized GenericEventKind event: %s", new String(serializedEvent)));
    return () -> ByteBuffer.wrap(serializedEvent);
  }

  @Override
  public String getId() {
    return eventId;
  }
}
