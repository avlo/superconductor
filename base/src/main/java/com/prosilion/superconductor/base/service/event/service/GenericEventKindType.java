package com.prosilion.superconductor.base.service.event.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.tag.BaseTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.nostr.user.Signature;
import java.beans.Transient;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public record GenericEventKindType(
    @NonNull @JsonIgnore GenericEventKindIF genericEventKind,
    @NonNull @Getter @EqualsAndHashCode.Exclude @JsonIgnore KindTypeIF kindType) implements GenericEventKindTypeIF {

  @Override
  public String getId() {
    return genericEventKind.getId();
  }

  @Override
  @JsonProperty("pubkey")
  public PublicKey getPublicKey() {
    return genericEventKind.getPublicKey();
  }

  @Override
  @JsonProperty("created_at")
  public Long getCreatedAt() {
    return genericEventKind.getCreatedAt();
  }

  @Override
  public Kind getKind() {
    return genericEventKind.getKind();
  }

  @Override
  public List<BaseTag> getTags() {
    return genericEventKind.getTags();
  }

  @Override
  public String getContent() {
    return genericEventKind.getContent();
  }

  @Override
  @JsonProperty("sig")
  public Signature getSignature() {
    return genericEventKind.getSignature();
  }

  @Override
  public String toBech32() {
    return genericEventKind.toBech32();
  }

  @Transient
  @Override
  public Supplier<ByteBuffer> getByteArraySupplier() throws NostrException {
    return genericEventKind.getByteArraySupplier();
  }
}
