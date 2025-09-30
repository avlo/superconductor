package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.auth.AuthEventKinds;
import com.prosilion.superconductor.lib.redis.document.AuthDocumentIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AuthKindDocumentService implements AuthKindDocumentServiceIF {
  private final AuthDocumentServiceIF authDocumentServiceIF;
  private final AuthEventKinds authEventKinds;

  public AuthKindDocumentService(
      @NonNull AuthDocumentServiceIF authDocumentServiceIF,
      @NonNull AuthEventKinds authEventKinds) {
    this.authDocumentServiceIF = authDocumentServiceIF;
    this.authEventKinds = authEventKinds;
  }

  @Override
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    authDocumentServiceIF.save(sessionId, publicKey, challenge, createdAt);
  }

  @Override
  public AuthDocumentIF save(AuthDocumentIF authPersistantIF) {
    return authDocumentServiceIF.save(authPersistantIF);
  }

  @Override
  public Optional<AuthDocumentIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authDocumentServiceIF.findAuthPersistantBySessionId(sessionId);
  }

  @Override
  public Optional<AuthDocumentIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind) {
    return authEventKinds.has(kind) ? Optional.of(findAuthPersistantBySessionId(sessionId).orElseThrow()) : Optional.empty();
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authDocumentServiceIF.removeAuthPersistantBySessionId(sessionId);
  }
}
