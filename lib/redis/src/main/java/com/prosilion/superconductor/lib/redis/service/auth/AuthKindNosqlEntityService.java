package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.auth.AuthEventKinds;
import com.prosilion.superconductor.lib.redis.document.AuthNosqlIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AuthKindNosqlEntityService implements AuthKindNosqlEntityServiceIF {
  private final AuthNosqlEntityServiceIF authNosqlEntityServiceIF;
  private final AuthEventKinds authEventKinds;

  public AuthKindNosqlEntityService(
      @NonNull AuthNosqlEntityServiceIF authNosqlEntityServiceIF,
      @NonNull AuthEventKinds authEventKinds) {
    this.authNosqlEntityServiceIF = authNosqlEntityServiceIF;
    this.authEventKinds = authEventKinds;
  }

  @Override
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    authNosqlEntityServiceIF.save(sessionId, publicKey, challenge, createdAt);
  }

  @Override
  public AuthNosqlIF save(AuthNosqlIF authPersistantIF) {
    return authNosqlEntityServiceIF.save(authPersistantIF);
  }

  @Override
  public Optional<AuthNosqlIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authNosqlEntityServiceIF.findAuthPersistantBySessionId(sessionId);
  }

  @Override
  public Optional<AuthNosqlIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind) {
    return authEventKinds.has(kind) ? Optional.of(findAuthPersistantBySessionId(sessionId).orElseThrow()) : Optional.empty();
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authNosqlEntityServiceIF.removeAuthPersistantBySessionId(sessionId);
  }
}
