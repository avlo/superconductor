package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import java.util.List;
import org.springframework.lang.NonNull;

public interface EventKindServiceIF {
  void processIncomingEvent(EventIF event);
  List<Kind> getKinds();

  static GenericEventKind createGenericEventKindFromEntityIF(@NonNull EventIF event) {
    return new GenericEventKind(
        event.getId(),
        event.getPublicKey(),
        event.getCreatedAt(),
        event.getKind(),
        event.getTags(),
        event.getContent(),
        event.getSignature());
  }
}
