package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.event.service.GenericEventKind;
import com.prosilion.superconductor.base.service.event.service.GenericEventKindIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventEntityIF;

public record GenericEventKindDto(BaseEvent baseEvent) {
  public EventEntityIF convertDtoToEntity() {
    EventEntity e = new EventEntity(
        baseEvent.getId(),
        baseEvent.getKind().getValue(),
        baseEvent.getPublicKey().toString(),
        baseEvent.getCreatedAt(),
        baseEvent.getSignature().toString(),
        baseEvent.getContent());
    e.setTags(baseEvent.getTags());
    return e;
  }

  public GenericEventKindIF convertBaseEventToEventIF() {
    return new GenericEventKind(
        baseEvent.getId(),
        baseEvent.getPublicKey(),
        baseEvent.getCreatedAt(),
        baseEvent.getKind(),
        baseEvent.getTags(),
        baseEvent.getContent(),
        baseEvent.getSignature());
  }
}
