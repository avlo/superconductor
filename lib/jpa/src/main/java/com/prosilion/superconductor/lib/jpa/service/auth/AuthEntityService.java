package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.user.PublicKey;
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
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    save(new AuthEntity(sessionId, publicKey, challenge, createdAt));
  }

  @Override
  public Long save(AuthEntity authEntity) {
    removeAuthPersistantBySessionId(authEntity.getSessionId());
    return authEntityRepository.save(authEntity).getUid();
  }

  @Override
  public Optional<AuthEntity> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authEntityRepository.findBySessionId(sessionId);
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    findAuthPersistantBySessionId(sessionId).ifPresent(authEntityRepository::delete);
  }
}
