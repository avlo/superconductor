package com.prosilion.superconductor.lib.redis.service.auth;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.service.AuthKindPersistantServiceIF;
import com.prosilion.superconductor.lib.redis.document.AuthDocumentIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public interface AuthKindDocumentServiceIF extends AuthKindPersistantServiceIF<AuthDocumentIF, AuthDocumentIF> {
  Optional<AuthDocumentIF> findAuthPersistantBySessionIdAndKind(@NonNull String sessionId, @NonNull Kind kind);
}
