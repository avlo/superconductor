package com.prosilion.superconductor.lib.redis.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;

public record GenericDocumentKindDto(BaseEvent baseEvent) {
  public EventDocumentIF convertDtoToDocument() {
    EventDocument eventDocument = EventDocument.of(
        baseEvent.getId(),
        baseEvent.getKind().getValue(),
        baseEvent.getPublicKey().toString(),
        baseEvent.getCreatedAt(),
        baseEvent.getContent(),
        baseEvent.getSignature().toString());

    eventDocument.setTags(baseEvent.getTags());
    return eventDocument;
  }

  public EventIF convertBaseEventToEventIF() {
    return baseEvent.getGenericEventRecord();
  }
}
