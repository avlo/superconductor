package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.auth.AuthEventKinds;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntityIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class AuthKindEntityService implements AuthKindEntityServiceIF {
  private final AuthEntityServiceIF authEntityServiceIF;
  private final AuthEventKinds authEventKinds;

  public AuthKindEntityService(
      @NonNull AuthEntityServiceIF authEntityServiceIF,
      @NonNull AuthEventKinds authEventKinds) {
    this.authEntityServiceIF = authEntityServiceIF;
    this.authEventKinds = authEventKinds;
  }

  @Override
  public void save(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    authEntityServiceIF.save(sessionId, publicKey, challenge, createdAt);
  }

  @Override
  public Long save(@NonNull AuthEntityIF authEntityIF) {
    return authEntityServiceIF.save(authEntityIF);
  }

  @Override
  public Optional<AuthEntityIF> findAuthPersistantBySessionId(@NonNull String sessionId) {
    return authEntityServiceIF.findAuthPersistantBySessionId(sessionId);
  }

  @Override
  public Optional<AuthEntityIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind) {
    return authEventKinds.has(kind) ? Optional.of(findAuthPersistantBySessionId(sessionId).orElseThrow()) : Optional.empty();
  }

  @Override
  public void removeAuthPersistantBySessionId(@NonNull String sessionId) {
    authEntityServiceIF.removeAuthPersistantBySessionId(sessionId);
  }
}
