package com.prosilion.superconductor.pubsub;

import nostr.event.Kind;
import nostr.event.impl.GenericEvent;

public record AddNostrEvent<T extends GenericEvent>(
    Kind kind,
    Long id,
    T event
) {}
