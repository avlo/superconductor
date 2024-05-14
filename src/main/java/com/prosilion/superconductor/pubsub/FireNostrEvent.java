package com.prosilion.superconductor.pubsub;

import nostr.event.impl.GenericEvent;

public record FireNostrEvent<T extends GenericEvent>(Long subscriberId, T event) {
}
