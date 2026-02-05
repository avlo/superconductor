package com.prosilion.superconductor.base.service.event.auth;

import com.prosilion.nostr.enums.Kind;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthKindPersistantServiceIF<T, U extends AuthPersistantIF> extends AuthPersistantServiceIF<T, U> {
  Optional<U> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind);
}
