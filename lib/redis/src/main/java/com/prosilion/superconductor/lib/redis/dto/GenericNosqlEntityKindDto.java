package com.prosilion.superconductor.lib.redis.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.lib.redis.document.EventNosqlEntity;
import com.prosilion.superconductor.lib.redis.document.EventNosqlEntityIF;

public record GenericNosqlEntityKindDto(BaseEvent baseEvent) {
  public EventNosqlEntityIF convertDtoToNosqlEntity() {
    EventNosqlEntity eventNosqlEntity = EventNosqlEntity.of(
        baseEvent.getId(),
        baseEvent.getKind().getValue(),
        baseEvent.getPublicKey().toString(),
        baseEvent.getCreatedAt(),
        baseEvent.getContent(),
        baseEvent.getSignature().toString());

    eventNosqlEntity.setTags(baseEvent.getTags());
    return eventNosqlEntity;
  }

  public EventIF convertBaseEventToEventIF() {
    return baseEvent.getGenericEventRecord();
  }
}
