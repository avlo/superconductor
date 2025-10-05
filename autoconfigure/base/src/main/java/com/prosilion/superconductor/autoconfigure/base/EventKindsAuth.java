package com.prosilion.superconductor.autoconfigure.base;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.auth.EventKindsAuthIF;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public class EventKindsAuth implements EventKindsAuthIF {
  private final List<Kind> eventAuthenticationKinds;

  public EventKindsAuth(@NonNull List<Kind> eventAuthenticationKinds) {
    this.eventAuthenticationKinds = Optional.of(eventAuthenticationKinds).stream().flatMap(Collection::stream).toList();
  }

  @Override
  public boolean has(@NonNull Kind kind) {
    return eventAuthenticationKinds.contains(kind);
  }
}
