package com.prosilion.superconductor.lib.redis.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;

public record GenericDocumentKindDto(BaseEvent baseEvent) {
  public EventDocumentIF convertDtoToDocument() {
    EventDocument eventDocument = EventDocument.of(
        baseEvent.getEventId(),
        baseEvent.getKind().getValue(),
        baseEvent.getPublicKey().toString(),
        baseEvent.getCreatedAt(),
        baseEvent.getContent(),
        baseEvent.getSignature().toString());

    eventDocument.setTags(baseEvent.getTags());
    return eventDocument;
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
