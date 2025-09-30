package com.prosilion.superconductor.base.service.event.auth;

import com.prosilion.nostr.enums.Kind;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public class AuthEventKinds {
  private final List<Kind> authenticationEventKinds;

  public AuthEventKinds(@NonNull List<Kind> authenticationEventKinds) {
    this.authenticationEventKinds = Optional.ofNullable(authenticationEventKinds).stream().flatMap(Collection::stream).toList();
  }

  public boolean has(@NonNull Kind kind) {
    return authenticationEventKinds.contains(kind);
  }
}
