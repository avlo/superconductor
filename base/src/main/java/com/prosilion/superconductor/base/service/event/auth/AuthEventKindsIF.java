package com.prosilion.superconductor.base.service.event.auth;

import com.prosilion.nostr.enums.Kind;
import org.springframework.lang.NonNull;

public interface AuthEventKindsIF {
  boolean has(@NonNull Kind kind);
}
