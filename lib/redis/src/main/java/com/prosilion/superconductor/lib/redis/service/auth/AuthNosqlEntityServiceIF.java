package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import com.prosilion.superconductor.lib.redis.document.AuthNosqlIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthNosqlEntityServiceIF extends AuthPersistantServiceIF<AuthNosqlIF, AuthNosqlIF> {
  AuthNosqlIF save(AuthNosqlIF authEntity);
  Optional<AuthNosqlIF> findAuthPersistantBySessionId(@NonNull String sessionId);
}
