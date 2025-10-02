package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.document.AuthNosql;
import com.prosilion.superconductor.lib.redis.document.AuthNosqlIF;
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
        AuthNosql.of(
            sessionId,
            publicKey.toString(),
            challenge,
            createdAt));
  }

  @Override
  public AuthNosqlIF save(AuthNosqlIF authPersistantIF) {
    removeAuthPersistantBySessionId(authPersistantIF.getSessionId());
    AuthNosql authNosql = AuthNosql.of(
        authPersistantIF.getSessionId(),
        authPersistantIF.getPublicKey(),
        authPersistantIF.getChallenge(),
        authPersistantIF.getCreatedAt()
    );
    return authNosqlEntityRepository.save(authNosql);
  }

  @Override
  public Optional<AuthNosqlIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authNosqlEntityRepository.findBySessionId(sessionId).map(AuthNosqlIF.class::cast);
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authNosqlEntityRepository.findBySessionId(sessionId).ifPresent(authNosqlEntityRepository::delete);
  }
}
