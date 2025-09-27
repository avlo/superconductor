package com.prosilion.superconductor.base.service.event.auth;

public interface AuthPersistantIF {
  String getSessionId();
  String getPublicKey();
  String getChallenge();
  Long getCreatedAt();
}
