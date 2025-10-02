package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.entity.AuthNosqlEntity;
import com.prosilion.superconductor.lib.redis.entity.AuthNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.repository.auth.AuthNosqlEntityRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AuthNosqlEntityService implements AuthNosqlEntityServiceIF {
  private final AuthNosqlEntityRepository authNosqlEntityRepository;

  public AuthNosqlEntityService(@NonNull AuthNosqlEntityRepository authNosqlEntityRepository) {
    this.authNosqlEntityRepository = authNosqlEntityRepository;
  }

  @Override
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    save(
        AuthNosqlEntity.of(
            sessionId,
            publicKey.toString(),
            challenge,
            createdAt));
  }

  @Override
  public AuthNosqlEntityIF save(AuthNosqlEntityIF authPersistantIF) {
    removeAuthPersistantBySessionId(authPersistantIF.getSessionId());
    AuthNosqlEntity authNosql = AuthNosqlEntity.of(
        authPersistantIF.getSessionId(),
        authPersistantIF.getPublicKey(),
        authPersistantIF.getChallenge(),
        authPersistantIF.getCreatedAt()
    );
    return authNosqlEntityRepository.save(authNosql);
  }

  @Override
  public Optional<AuthNosqlEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authNosqlEntityRepository.findBySessionId(sessionId).map(AuthNosqlEntityIF.class::cast);
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authNosqlEntityRepository.findBySessionId(sessionId).ifPresent(authNosqlEntityRepository::delete);
  }
}
