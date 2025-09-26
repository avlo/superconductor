package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthEntityServiceIF extends AuthPersistantServiceIF<Long, AuthEntity> {
  Long save(AuthEntity authEntity);
  Optional<AuthEntity> findAuthPersistantBySessionId(@NonNull String sessionId);
}
