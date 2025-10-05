package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.auth.EventKindsAuthIF;
import com.prosilion.superconductor.lib.redis.entity.AuthNosqlEntityIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AuthKindNosqlEntityService implements AuthKindNosqlEntityServiceIF {
  private final AuthNosqlEntityServiceIF authNosqlEntityServiceIF;
  private final EventKindsAuthIF eventKindsAuthIF;

  public AuthKindNosqlEntityService(
      @NonNull AuthNosqlEntityServiceIF authNosqlEntityServiceIF,
      @NonNull EventKindsAuthIF eventKindsAuthIF) {
    this.authNosqlEntityServiceIF = authNosqlEntityServiceIF;
    this.eventKindsAuthIF = eventKindsAuthIF;
  }

  @Override
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    authNosqlEntityServiceIF.save(sessionId, publicKey, challenge, createdAt);
  }

  @Override
  public AuthNosqlEntityIF save(AuthNosqlEntityIF authPersistantIF) {
    return authNosqlEntityServiceIF.save(authPersistantIF);
  }

  @Override
  public Optional<AuthNosqlEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authNosqlEntityServiceIF.findAuthPersistantBySessionId(sessionId);
  }

  @Override
  public Optional<AuthNosqlEntityIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind) {
    return eventKindsAuthIF.has(kind) ? Optional.of(findAuthPersistantBySessionId(sessionId).orElseThrow()) : Optional.empty();
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authNosqlEntityServiceIF.removeAuthPersistantBySessionId(sessionId);
  }
}
