package com.prosilion.superconductor.lib.redis.document;

import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.auth.AuthPersistantIF;

public interface AuthDocumentIF extends AuthPersistantIF {
  String getSessionId();
  String getPublicKey();
  String getChallenge();
  Long getCreatedAt();
}
