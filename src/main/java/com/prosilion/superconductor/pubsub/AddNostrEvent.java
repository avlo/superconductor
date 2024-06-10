package com.prosilion.superconductor.pubsub;

import nostr.event.Kind;
import nostr.event.impl.GenericEvent;

public record AddNostrEvent<T extends GenericEvent>(
//    TODO: IDE notes no usages of kind and id, investigate
    Kind kind,
    Long id,
    T event
) {}
