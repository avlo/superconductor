package com.prosilion.nostrrelay.pubsub;

import lombok.Getter;
import lombok.Setter;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;

@Setter
@Getter
public class AddNostrEvent<T extends GenericEvent> {
  private final Long id;
  private final T event;
  private final Kind kind;

  public AddNostrEvent(Long id, T event, Kind kind) {
    this.id = id;
    this.event = event;
    this.kind = kind;
  }
}
