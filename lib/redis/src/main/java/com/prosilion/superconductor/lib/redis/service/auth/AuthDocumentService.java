package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.document.AuthDocument;
import com.prosilion.superconductor.lib.redis.document.AuthDocumentIF;
import com.prosilion.superconductor.lib.redis.repository.auth.AuthDocumentRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnExpression("${superconductor.auth.req.active:true} || ${superconductor.auth.event.active:true}")
public class AuthDocumentService implements AuthDocumentServiceIF {
  private final AuthDocumentRepository authDocumentRepository;

  public AuthDocumentService(@NonNull AuthDocumentRepository authDocumentRepository) {
    this.authDocumentRepository = authDocumentRepository;
  }

  @Override
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    save(
        AuthDocument.of(
            sessionId,
            publicKey.toString(),
            challenge,
            createdAt));
  }

  @Override
  public AuthDocumentIF save(AuthDocumentIF authPersistantIF) {
    removeAuthPersistantBySessionId(authPersistantIF.getSessionId());
    AuthDocument authDocument = AuthDocument.of(
        authPersistantIF.getSessionId(),
        authPersistantIF.getPublicKey(),
        authPersistantIF.getChallenge(),
        authPersistantIF.getCreatedAt()
    );
    return authDocumentRepository.save(authDocument);
  }

  @Override
  public Optional<AuthDocumentIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return Optional.of(authDocumentRepository.findBySessionId(sessionId).map(AuthDocumentIF.class::cast)).orElse(Optional.empty());
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authDocumentRepository.findBySessionId(sessionId).ifPresent(authDocumentRepository::delete);
  }
}
