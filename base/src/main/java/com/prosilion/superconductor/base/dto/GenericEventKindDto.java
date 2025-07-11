package com.prosilion.superconductor.base.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.base.entity.EventEntity;

public record GenericEventKindDto(BaseEvent event) {
  public EventEntity convertDtoToEntity() {
    return new EventEntity(
        event.getId(),
        event.getKind().getValue(),
        event.getPublicKey().toString(),
        event.getCreatedAt(),
        event.getSignature(),
        event.getContent());
  }

  public GenericEventKindIF convertBaseEventToGenericEventKindIF() {
    return new GenericEventKind(
        event.getId(),
        event.getPublicKey(),
        event.getCreatedAt(),
        event.getKind(),
        event.getTags(),
        event.getContent(),
        Signature.fromString(event.getSignature()));
  }
}
