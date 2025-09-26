package com.prosilion.superconductor.base.service.event.service;

import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.auth.AuthPersistantIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthPersistantServiceIF<T, U extends AuthPersistantIF> {
  void save(String sessionId, PublicKey publicKey, String challenge, Long createdAt);
  T save(U authPersistantIF);
  Optional<U> findAuthPersistantBySessionId(@NonNull String sessionId);
  void removeAuthPersistantBySessionId(@NonNull String sessionId);
}
