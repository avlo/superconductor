package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import com.prosilion.superconductor.lib.redis.document.AuthDocumentIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthDocumentServiceIF extends AuthPersistantServiceIF<AuthDocumentIF, AuthDocumentIF> {
  AuthDocumentIF save(AuthDocumentIF authEntity);
  Optional<AuthDocumentIF> findAuthPersistantBySessionId(@NonNull String sessionId);
}
