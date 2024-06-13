package com.prosilion.superconductor.pubsub;

import lombok.NonNull;
import nostr.event.impl.GenericEvent;

public record FireNostrEvent<T extends GenericEvent>(@NonNull Long subscriberId, @NonNull T event) {
}
