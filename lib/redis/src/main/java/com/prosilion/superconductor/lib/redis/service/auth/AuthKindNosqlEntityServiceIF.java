package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.service.AuthKindPersistantServiceIF;
import com.prosilion.superconductor.lib.redis.entity.AuthNosqlEntityIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthKindNosqlEntityServiceIF extends AuthKindPersistantServiceIF<AuthNosqlEntityIF, AuthNosqlEntityIF> {
  Optional<AuthNosqlEntityIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind);
}
