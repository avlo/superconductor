package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthEntityServiceIF {
  Long save(AuthEntity authEntity);
  Optional<AuthEntity> findAuthEntityBySessionId(@NonNull String sessionId);
  void removeAuthEntityBySessionId(@NonNull String sessionId);
}
