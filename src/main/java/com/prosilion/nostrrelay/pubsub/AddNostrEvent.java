package com.prosilion.nostrrelay.pubsub;

import lombok.Getter;
import lombok.Setter;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
public class AddNostrEvent<T extends GenericEvent> {
  private final Kind kind;
  private final Map<Long, T> eventIdEventMap;
  private final Long id;

  public AddNostrEvent(Long id, T event, Kind kind) {
    this.kind = kind;
    this.id = id;
    this.eventIdEventMap = new HashMap<>();
    this.eventIdEventMap.put(id, event);
  }
}
