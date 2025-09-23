package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthEntityRepository;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public class AuthEntityService implements AuthEntityServiceIF {
  private final AuthEntityRepository authEntityRepository;

  public AuthEntityService(AuthEntityRepository authEntityRepository) {
    this.authEntityRepository = authEntityRepository;
  }

  @Override
  public Long save(AuthEntity authEntity) {
    removeAuthEntityBySessionId(authEntity.getSessionId());
    return authEntityRepository.save(authEntity).getId();
  }

  @Override
  public Optional<AuthEntity> findAuthEntityBySessionId(@NonNull String sessionId) {
    return authEntityRepository.findBySessionId(sessionId);
  }

  @Override
  public void removeAuthEntityBySessionId(@NonNull String sessionId) {
    findAuthEntityBySessionId(sessionId).ifPresent(authEntityRepository::delete);
  }
}
