package com.prosilion.superconductor.base.service.event.auth;

import com.prosilion.nostr.enums.Kind;
import lombok.NonNull;

public interface EventKindsAuthIF {
  boolean has(@NonNull Kind kind);
}
