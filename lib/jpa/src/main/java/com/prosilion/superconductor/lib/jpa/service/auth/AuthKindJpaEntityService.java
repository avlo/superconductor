package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.auth.EventKindsAuth;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthJpaEntityIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AuthKindJpaEntityService implements AuthKindJpaEntityServiceIF {
  private final AuthJpaEntityServiceIF authJpaEntityServiceIF;
  private final EventKindsAuth eventKindsAuth;

  public AuthKindJpaEntityService(
      @NonNull AuthJpaEntityServiceIF authJpaEntityServiceIF,
      @NonNull EventKindsAuth eventKindsAuth) {
    this.authJpaEntityServiceIF = authJpaEntityServiceIF;
    this.eventKindsAuth = eventKindsAuth;
  }

  @Override
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    authJpaEntityServiceIF.save(sessionId, publicKey, challenge, createdAt);
  }

  @Override
  public Long save(@NonNull AuthJpaEntityIF authJpaEntityIF) {
    return authJpaEntityServiceIF.save(authJpaEntityIF);
  }

  @Override
  public Optional<AuthJpaEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authJpaEntityServiceIF.findAuthPersistantBySessionId(sessionId);
  }

  @Override
  public Optional<AuthJpaEntityIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind) {
    return eventKindsAuth.has(kind) ? Optional.of(findAuthPersistantBySessionId(sessionId).orElseThrow()) : Optional.empty();
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authJpaEntityServiceIF.removeAuthPersistantBySessionId(sessionId);
  }
}
