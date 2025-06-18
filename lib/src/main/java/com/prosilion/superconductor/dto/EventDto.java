package com.prosilion.superconductor.dto;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventDto;
import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.entity.EventEntity;

public record EventDto(BaseEvent event) {

  public EventEntity convertDtoToEntity() {
    return new EventEntity(
        event.getId(),
        event.getKind().getValue(),
        event.getPublicKey().toString(),
        event.getCreatedAt(),
        event.getSignature(),
        event.getContent());
  }

  public GenericEventDtoIF convertBaseEventToDto() {
    return new GenericEventDto(
        event.getId(),
        event.getPublicKey(),
        event.getCreatedAt(),
        event.getKind(),
        event.getTags(),
        event.getContent(),
        Signature.fromString(event.getSignature()));
  }
}
