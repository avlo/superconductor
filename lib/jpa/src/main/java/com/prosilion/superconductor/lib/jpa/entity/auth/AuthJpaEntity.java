package com.prosilion.superconductor.lib.jpa.entity.auth;

import com.prosilion.nostr.user.PublicKey;
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
import org.springframework.lang.NonNull;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "auth")
public class AuthJpaEntity implements AuthJpaEntityIF {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long uid;

  //  TODO: might just need sessionId, revisit
  private String publicKey;
  private String sessionId;
  private String challenge;
  private Long createdAt;

  @Value("${superconductor.relay.url}")
  @Transient
  private String relayUrl;

  public AuthJpaEntity(@NonNull String sessionId, @NonNull PublicKey publicKey, @NonNull String challenge, @NonNull Long createdAt) {
    this.sessionId = sessionId;
    this.publicKey = publicKey.toString();
    this.challenge = challenge;
    this.createdAt = createdAt;
  }
}
