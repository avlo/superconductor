package com.prosilion.nostrrelay.pubsub;

import lombok.Getter;
import lombok.Setter;
import nostr.event.impl.GenericEvent;

@Setter
@Getter
public class FireNostrEvent<T extends GenericEvent> {
  private final Long subscriberId;
  private final T event;

  public FireNostrEvent(Long subscriberId, T event) {
    this.subscriberId = subscriberId;
    this.event = event;
  }
}
