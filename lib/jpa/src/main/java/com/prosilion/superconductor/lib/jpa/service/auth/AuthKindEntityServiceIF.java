package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntityIF;
import java.util.Optional;

public interface AuthKindEntityServiceIF extends AuthEntityServiceIF {
  Optional<AuthEntityIF> findAuthPersistantBySessionIdAndKind(String sessionId, Kind kind);
}
