package com.prosilion.superconductor.base.service.request.pubsub;

import com.prosilion.nostr.event.EventIF;
import org.springframework.lang.NonNull;

public record FireNostrEvent(@NonNull Long subscriptionHash, @NonNull String subscriberId, @NonNull EventIF event) {
}
