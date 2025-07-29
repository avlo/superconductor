package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;

public record GenericEventKindDto(BaseEvent baseEvent) {
  public EventEntityIF convertDtoToEntity() {
    return new EventEntity(
        baseEvent.getEventId(),
        baseEvent.getKind().getValue(),
        baseEvent.getPublicKey().toString(),
        baseEvent.getCreatedAt(),
        baseEvent.getSignature().toString(),
        baseEvent.getContent());
  }

  public GenericEventKindIF convertBaseEventToEventIF() {
    return new GenericEventKind(
        baseEvent.getEventId(),
        baseEvent.getPublicKey(),
        baseEvent.getCreatedAt(),
        baseEvent.getKind(),
        baseEvent.getTags(),
        baseEvent.getContent(),
        baseEvent.getSignature());
  }
}
