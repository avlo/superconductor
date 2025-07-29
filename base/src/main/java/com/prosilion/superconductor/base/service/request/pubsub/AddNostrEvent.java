package com.prosilion.superconductor.base.service.request.pubsub;

import com.prosilion.nostr.event.EventIF;
import org.springframework.lang.NonNull;

public record AddNostrEvent(@NonNull EventIF event) {
}
