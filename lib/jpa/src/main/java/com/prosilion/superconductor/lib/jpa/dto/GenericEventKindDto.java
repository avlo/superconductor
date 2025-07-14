package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.lib.jpa.entity.EventEntity;

public record GenericEventKindDto(BaseEvent baseEvent) {
  public EventEntity convertDtoToEntity() {
    return new EventEntity(
        baseEvent.getId(),
        baseEvent.getKind().getValue(),
        baseEvent.getPublicKey().toString(),
        baseEvent.getCreatedAt(),
        baseEvent.getSignature(),
        baseEvent.getContent());
  }

  public GenericEventKindIF convertBaseEventToGenericEventKindIF() {
    return new GenericEventKind(
        baseEvent.getId(),
        baseEvent.getPublicKey(),
        baseEvent.getCreatedAt(),
        baseEvent.getKind(),
        baseEvent.getTags(),
        baseEvent.getContent(),
        Signature.fromString(baseEvent.getSignature()));
  }
}
