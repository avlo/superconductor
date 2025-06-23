package com.prosilion.superconductor.dto;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.GenericEventKind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindType;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.user.Signature;
import com.prosilion.superconductor.entity.EventEntity;
import java.util.Arrays;
import java.util.List;

public record GenericEventKindTypeDto(BaseEvent event, List<KindTypeIF> kindTypes) {

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
    return new GenericEventKindType(convertBaseEventToGenericEventKindIF(), kindTypes);
  }

  private GenericEventKindIF convertBaseEventToGenericEventKindIF() {
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
    List<KindTypeIF> kindTypes1 = kindTypes;
    List<Kind> kindList = kindTypes1.stream().map(KindTypeIF::getKind).toList();
    List<Kind> kindDistinctList = kindTypes1.stream().map(KindTypeIF::getKind).distinct().toList();
    assert
        kindDistinctList.stream().anyMatch(d -> {
          Kind kind = genericEventKind.getKind();
          boolean b = kind.equals(d);
          return b;
        }) :
        new IllegalStateException(
            String.format(
                "KindTypes [%s] does not contain [%s]",
                Arrays.toString(kindList.toArray()),
                genericEventKind.getKind()));

    List<AddressTag> typeSpecificTags = Filterable.getTypeSpecificTags(AddressTag.class, genericEventKind);

    assert !typeSpecificTags.isEmpty() :
        new IllegalStateException("AddressTag must be present");

    return new GenericEventKindType(genericEventKind, kindTypes);
  }
}
