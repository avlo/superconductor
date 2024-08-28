package com.prosilion.superconductor.service.request.pubsub;

import lombok.NonNull;
import nostr.event.impl.GenericEvent;

public record AddNostrEvent<T extends GenericEvent>(@NonNull T event) {}
