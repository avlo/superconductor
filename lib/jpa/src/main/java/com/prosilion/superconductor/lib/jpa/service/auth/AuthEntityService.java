package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntityIF;
import com.prosilion.superconductor.lib.jpa.repository.auth.AuthEntityRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
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
  public Long save(AuthEntityIF authEntityIF) {
    removeAuthPersistantBySessionId(authEntityIF.getSessionId());
    AuthEntity authEntity = new AuthEntity(
        authEntityIF.getSessionId(),
        new PublicKey(authEntityIF.getPublicKey()),
        authEntityIF.getChallenge(),
        authEntityIF.getCreatedAt());
    return authEntityRepository.save(authEntity).getUid();
  }

  @Override
  public Optional<AuthEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authEntityRepository.findBySessionId(sessionId).map(AuthEntityIF.class::cast);
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authEntityRepository.findBySessionId(sessionId).ifPresent(authEntityRepository::delete);
  }
}
