package com.prosilion.superconductor.lib.redis.dto;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.event.service.GenericEventKind;
import com.prosilion.superconductor.base.service.event.service.GenericEventKindType;
import com.prosilion.superconductor.base.service.event.service.GenericEventKindTypeIF;
import com.prosilion.superconductor.lib.redis.document.EventDocument;
import com.prosilion.superconductor.lib.redis.document.EventDocumentIF;

public record GenericDocumentKindTypeDto(BaseEvent baseEvent, KindTypeIF kindType) {

  public EventDocumentIF convertDtoToEntity() {
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

  public GenericEventKindTypeIF convertBaseEventToGenericEventKindTypeIF() {
    return new GenericEventKindType(
        new GenericEventKind(
            baseEvent.getId(),
            baseEvent.getPublicKey(),
            baseEvent.getCreatedAt(),
            baseEvent.getKind(),
            baseEvent.getTags(),
            baseEvent.getContent(),
            baseEvent.getSignature()),
        kindType);
  }
}
