package com.prosilion.superconductor.lib.redis.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.lib.redis.document.EventDocument;

public record GenericDocumentKindDto(BaseEvent baseEvent) {
  public EventDocument convertDtoToDocument() {
    EventDocument eventDocument = EventDocument.of(
        baseEvent.getId(),
        baseEvent.getKind().getValue(),
        baseEvent.getPublicKey().toString(),
        baseEvent.getCreatedAt(),
        baseEvent.getContent(),
        baseEvent.getSignature());

    eventDocument.setTags(baseEvent.getTags());
    return eventDocument;
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
