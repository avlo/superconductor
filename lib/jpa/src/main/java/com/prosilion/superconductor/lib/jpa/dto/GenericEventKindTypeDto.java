package com.prosilion.superconductor.lib.jpa.dto;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.lib.jpa.entity.EventEntity;

public record GenericEventKindTypeDto(BaseEvent event, KindTypeIF kindType) {

  public EventEntity convertDtoToEntity() {
    return new EventEntity(
        event.getId(),
        event.getKind().getValue(),
        event.getPublicKey().toString(),
        event.getCreatedAt(),
        event.getSignature(),
        event.getContent());
  }

  public GenericEventKindTypeIF convertBaseEventToGenericEventKindTypeIF() {
    return new GenericEventKindType(
        event.getId(),
        event.getPublicKey(),
        event.getCreatedAt(),
        event.getKind(),
        event.getTags(),
        event.getContent(),
        Signature.fromString(event.getSignature()),
        kindType);
  }
}
