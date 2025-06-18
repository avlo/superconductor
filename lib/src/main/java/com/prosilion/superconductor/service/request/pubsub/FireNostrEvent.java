package com.prosilion.superconductor.service.request.pubsub;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventDtoIF;

public record FireNostrEvent<T extends GenericEventDtoIF>(@NonNull Long subscriptionHash, @NonNull String subscriberId, @NonNull T event) {
}
