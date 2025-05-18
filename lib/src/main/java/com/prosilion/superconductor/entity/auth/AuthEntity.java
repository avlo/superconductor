package com.prosilion.superconductor.entity.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import nostr.api.NIP42;
import nostr.base.PublicKey;
import nostr.base.Relay;
import nostr.event.BaseTag;
import nostr.event.impl.CanonicalAuthenticationEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.GenericTag;
import nostr.event.tag.RelaysTag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "auth")
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public class AuthEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  //  TODO: might just need sessionId, revisit
  private String pubKey;
  private String sessionId;
  private String challenge;
  private Long createdAt;

  @Value("${superconductor.relay.url}")
  @Transient
  private String relayUrl;

  public AuthEntity(@NonNull String pubKey, @NonNull String challenge, @NonNull String sessionId, @NonNull Long createdAt) {
    this.pubKey = pubKey;
    this.challenge = challenge;
    this.sessionId = sessionId;
    this.createdAt = createdAt;
  }

  public <T extends GenericEvent> T convertEntityToDto() {
    BaseTag relayTag = BaseTag.create("relay",relayUrl);
    BaseTag challengeTag = BaseTag.create("challenge", challenge);
    return (T) new CanonicalAuthenticationEvent(new PublicKey(pubKey), List.of(relayTag, challengeTag), "");
  }
}
