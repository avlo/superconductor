package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;

public record GenericEventKindDto(BaseEvent baseEvent) {
  public EventJpaEntityIF convertDtoToEntity() {
    EventJpaEntity e = new EventJpaEntity(
        baseEvent.getId(),
        baseEvent.getKind().getValue(),
        baseEvent.getPublicKey().toString(),
        baseEvent.getCreatedAt(),
        baseEvent.getSignature().toString(),
        baseEvent.getContent());
    e.setTags(baseEvent.getTags());
    return e;
  }

  public GenericEventRecord convertBaseEventToEventIF() {
    return baseEvent.asGenericEventRecord();
  }
}
