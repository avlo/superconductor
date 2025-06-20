package com.prosilion.superconductor.dto;

import com.prosilion.nostr.enums.KindType;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindType;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.entity.EventEntity;
import java.util.Arrays;
import java.util.List;

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

  public GenericEventKindIF convertBaseEventToDto() {
    return checkForType(
        new GenericEventKind(
            event.getId(),
            event.getPublicKey(),
            event.getCreatedAt(),
            event.getKind(),
            event.getTags(),
            event.getContent(),
            Signature.fromString(event.getSignature())));
  }

  private GenericEventKindIF checkForType(GenericEventKindIF genericEventKind) {
    if (Arrays.stream(KindType.values()).map(KindType::getKind).distinct().noneMatch(genericEventKind.getKind()::equals))
      return genericEventKind;

    List<AddressTag> typeSpecificTags = Filterable.getTypeSpecificTags(AddressTag.class, genericEventKind);

    if (typeSpecificTags.isEmpty())
      return genericEventKind;

    return new GenericEventKindType(genericEventKind);
  }
}
