package com.prosilion.superconductor.service.request.pubsub;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventKindIF;

public record FireNostrEvent<T extends GenericEventKindIF>(@NonNull Long subscriptionHash, @NonNull String subscriberId, @NonNull T event) {
}
