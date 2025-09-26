package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthEntityRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnExpression("${superconductor.auth.req.active:true} || ${superconductor.auth.event.active:true}")
public class AuthEntityService implements AuthEntityServiceIF {
  private final AuthEntityRepository authEntityRepository;

  public AuthEntityService(@NonNull AuthEntityRepository authEntityRepository) {
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
