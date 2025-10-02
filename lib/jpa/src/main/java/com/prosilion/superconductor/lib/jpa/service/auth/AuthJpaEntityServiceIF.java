package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthJpaEntityIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthJpaEntityServiceIF extends AuthPersistantServiceIF<Long, AuthJpaEntityIF> {
  Long save(AuthJpaEntityIF authEntity);
  Optional<AuthJpaEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId);
}
