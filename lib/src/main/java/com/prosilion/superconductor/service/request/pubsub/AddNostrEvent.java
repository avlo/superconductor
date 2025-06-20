package com.prosilion.superconductor.service.request.pubsub;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventKindIF;

public record AddNostrEvent<T extends GenericEventKindIF>(@NonNull T event) {}
