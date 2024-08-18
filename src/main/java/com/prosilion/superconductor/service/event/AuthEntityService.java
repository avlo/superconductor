package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.entity.auth.AuthEntity;
import com.prosilion.superconductor.repository.auth.AuthEntityRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthEntityService {
  private final AuthEntityRepository authEntityRepository;

  public AuthEntityService(AuthEntityRepository authEntityRepository) {
    this.authEntityRepository = authEntityRepository;
  }

  public Long save(AuthEntity authEntity) {
    return authEntityRepository.save(authEntity).getId();
  }

  public Optional<AuthEntity> findAuthEntityBySessionId(@NonNull String sessionId) {
    return authEntityRepository.findBySessionId(sessionId);
  }
}
