package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.superconductor.base.service.event.auth.AuthPersistantIF;
import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntityIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthEntityServiceIF extends AuthPersistantServiceIF<Long, AuthEntityIF> {
  Long save(AuthEntityIF authEntity);
  Optional<AuthEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId);
}
