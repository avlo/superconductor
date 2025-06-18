//package com.prosilion.superconductor.entity.auth;
//
//import com.prosilion.nostr.event.CanonicalAuthenticationEvent;
//import com.prosilion.nostr.event.GenericEventDto;
//import com.prosilion.nostr.event.internal.Relay;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import jakarta.persistence.Transient;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import org.springframework.lang.NonNull;
//import lombok.Setter;
//import com.prosilion.nostr.user.PublicKey;
//import com.prosilion.nostr.event.GenericEventDtoIF;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//
//@Setter
//@Getter
//@NoArgsConstructor
//@Entity
//@Table(name = "auth")
//@ConditionalOnProperty(
//    name = "superconductor.auth.active",
//    havingValue = "true")
//public class AuthEntity {
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  private long id;
//
//  //  TODO: might just need sessionId, revisit
//  private String pubKey;
//  private String sessionId;
//  private String challenge;
//  private Long createdAt;
//
//  @Value("${superconductor.relay.url}")
//  @Transient
//  private String relayUrl;
//
//  public AuthEntity(@NonNull String pubKey, @NonNull String challenge, @NonNull String sessionId, @NonNull Long createdAt) {
//    this.pubKey = pubKey;
//    this.challenge = challenge;
//    this.sessionId = sessionId;
//    this.createdAt = createdAt;
//  }
//
//  public <T extends GenericEventDtoIF> T convertEntityToDto() {
//    GenericEventDtoIF dtoIF = new GenericEventDto(
//        id, new PublicKey(pubKey), challenge, new Relay(relayUrl)
//    );
//    return (T) new CanonicalAuthenticationEvent(new PublicKey(pubKey), challenge, new Relay(relayUrl));
//  }
//}
