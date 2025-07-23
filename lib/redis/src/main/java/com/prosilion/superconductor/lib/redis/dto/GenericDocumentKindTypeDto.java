package com.prosilion.superconductor.lib.redis.dto;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.lib.redis.document.EventDocument;

public record GenericDocumentKindTypeDto(BaseEvent baseEvent, KindTypeIF kindType) {

  public EventDocument convertDtoToEntity() {
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

  public GenericEventKindTypeIF convertBaseEventToGenericEventKindTypeIF() {
    return new GenericEventKindType(
        baseEvent.getId(),
        baseEvent.getPublicKey(),
        baseEvent.getCreatedAt(),
        baseEvent.getKind(),
        baseEvent.getTags(),
        baseEvent.getContent(),
        Signature.fromString(baseEvent.getSignature()),
        kindType);
  }
}
