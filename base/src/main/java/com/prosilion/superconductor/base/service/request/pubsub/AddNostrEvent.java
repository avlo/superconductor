package com.prosilion.superconductor.base.service.request.pubsub;

import com.prosilion.nostr.event.EventIF;
import lombok.NonNull;

public record AddNostrEvent(@NonNull EventIF event) {
}
