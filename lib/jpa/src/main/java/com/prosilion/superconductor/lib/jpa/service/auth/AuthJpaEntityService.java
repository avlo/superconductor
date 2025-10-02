package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthJpaEntity;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthJpaEntityRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AuthJpaEntityService implements AuthJpaEntityServiceIF {
  private final AuthJpaEntityRepository authJpaEntityRepository;

  public AuthJpaEntityService(@NonNull AuthJpaEntityRepository authJpaEntityRepository) {
    this.authJpaEntityRepository = authJpaEntityRepository;
  }

  @Override
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    save(new AuthJpaEntity(sessionId, publicKey, challenge, createdAt));
  }

  @Override
  public Long save(AuthJpaEntityIF authJpaEntityIF) {
    removeAuthPersistantBySessionId(authJpaEntityIF.getSessionId());
    AuthJpaEntity authEntity = new AuthJpaEntity(
        authJpaEntityIF.getSessionId(),
        new PublicKey(authJpaEntityIF.getPublicKey()),
        authJpaEntityIF.getChallenge(),
        authJpaEntityIF.getCreatedAt());
    return authJpaEntityRepository.save(authEntity).getUid();
  }

  @Override
  public Optional<AuthJpaEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authJpaEntityRepository.findBySessionId(sessionId).map(AuthJpaEntityIF.class::cast);
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authJpaEntityRepository.findBySessionId(sessionId).ifPresent(authJpaEntityRepository::delete);
  }
}
