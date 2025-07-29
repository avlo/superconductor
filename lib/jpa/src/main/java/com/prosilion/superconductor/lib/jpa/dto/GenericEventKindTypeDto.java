package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.superconductor.lib.jpa.entity.EventEntity;

public record GenericEventKindTypeDto(BaseEvent event, KindTypeIF kindType) {

  public EventEntity convertDtoToEntity() {
    return new EventEntity(
        event.getEventId(),
        event.getKind().getValue(),
        event.getPublicKey().toString(),
        event.getCreatedAt(),
        event.getSignature().toString(),
        event.getContent());
  }

  public GenericEventKindTypeIF convertBaseEventToGenericEventKindTypeIF() {
    return new GenericEventKindType(
        new GenericEventKind(
            event.getEventId(),
            event.getPublicKey(),
            event.getCreatedAt(),
            event.getKind(),
            event.getTags(),
            event.getContent(),
            event.getSignature()),
        kindType);
  }
}
