package com.prosilion.superconductor.service.request.pubsub;

import org.springframework.lang.NonNull;
import com.prosilion.nostr.event.GenericEventDtoIF;

public record AddNostrEvent<T extends GenericEventDtoIF>(@NonNull T event) {}
