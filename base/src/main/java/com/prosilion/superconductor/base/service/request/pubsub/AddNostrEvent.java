package com.prosilion.superconductor.base.service.request.pubsub;

import com.prosilion.nostr.event.GenericEventKindIF;
import org.springframework.lang.NonNull;

public record AddNostrEvent(@NonNull GenericEventKindIF event) {}
