package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.service.AuthKindPersistantServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthJpaEntityIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthKindJpaEntityServiceIF extends AuthKindPersistantServiceIF<Long, AuthJpaEntityIF> {
  Optional<AuthJpaEntityIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind);
}
