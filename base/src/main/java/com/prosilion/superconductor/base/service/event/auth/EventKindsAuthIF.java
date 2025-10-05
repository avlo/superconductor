package com.prosilion.superconductor.base.service.event.auth;

import com.prosilion.nostr.enums.Kind;
import org.springframework.lang.NonNull;

public interface EventKindsAuthIF {
  boolean has(@NonNull Kind kind);
}
