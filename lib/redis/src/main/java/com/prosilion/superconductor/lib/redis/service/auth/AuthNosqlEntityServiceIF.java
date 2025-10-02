package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import com.prosilion.superconductor.lib.redis.entity.AuthNosqlEntityIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthNosqlEntityServiceIF extends AuthPersistantServiceIF<AuthNosqlEntityIF, AuthNosqlEntityIF> {
  AuthNosqlEntityIF save(AuthNosqlEntityIF authEntity);
  Optional<AuthNosqlEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId);
}
