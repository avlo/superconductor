package com.prosilion.superconductor.lib.jpa.entity.auth;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.lang.NonNull;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "auth")
@ConditionalOnExpression("${superconductor.auth.req.active:true} || ${superconductor.auth.event.active:true}")
public class AuthEntity implements AuthEntityIF {
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
}
