package com.prosilion.superconductor.util;

import com.prosilion.nostr.event.AddressableEvent;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import java.util.List;
import lombok.Getter;

@Getter
public class EventAttributesMap<T extends AddressableEvent> {
  private T event;
  private PublicKey creatorPublicKey;
  private IdentifierTag identifierTag;

  public EventAttributesMap(T event, PublicKey creatorPublicKey, IdentifierTag identifierTag) {
    this.event = event;
    this.creatorPublicKey = creatorPublicKey;
    this.identifierTag = identifierTag;
  }

  static public <T extends AddressableEvent> List<T> asEventList(List<EventAttributesMap<T>> list) {
    return list.stream().map(EventAttributesMap::getEvent).toList();
  }

  static public <T extends AddressableEvent> List<EventAttributesMap<T>> asEventAttributesMap(List<T> eventList) {
    return eventList.stream().map(addr ->
       new EventAttributesMap<>(
          addr,
          addr.getPublicKey(),
          addr.getIdentifierTag())).toList();
  }

  static public <T extends AddressableEvent> T getFirstByIdentifierTag(List<EventAttributesMap<T>> eventList, IdentifierTag identifierTag) {
    return eventList.stream()
       .filter(eaMap ->
          eaMap.getIdentifierTag().equals(identifierTag))
       .findFirst().orElseThrow()
       .getEvent();
  }
}
