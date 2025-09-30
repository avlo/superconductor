package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.lib.redis.document.AuthDocumentIF;
import java.util.Optional;

public interface AuthKindDocumentServiceIF extends AuthDocumentServiceIF {
  Optional<AuthDocumentIF> findAuthPersistantBySessionIdAndKind(String sessionId, Kind kind);
}
