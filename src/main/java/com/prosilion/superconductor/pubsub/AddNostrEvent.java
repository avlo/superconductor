package com.prosilion.superconductor.pubsub;

import lombok.NonNull;
import nostr.event.impl.GenericEvent;

public record AddNostrEvent<T extends GenericEvent>(@NonNull T event) {}
