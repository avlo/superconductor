package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.service.AuthKindPersistantServiceIF;
import com.prosilion.superconductor.lib.redis.document.AuthNosqlIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthKindNosqlEntityServiceIF extends AuthKindPersistantServiceIF<AuthNosqlIF, AuthNosqlIF> {
  Optional<AuthNosqlIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind);
}
