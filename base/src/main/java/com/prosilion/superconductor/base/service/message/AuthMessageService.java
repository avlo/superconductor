package com.prosilion.superconductor.base.service.message;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.message.CanonicalAuthenticationMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.event.auth.AuthPersistantIF;
import com.prosilion.superconductor.base.service.event.service.AuthPersistantServiceIF;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;

@Slf4j
public class AuthMessageService<T, U extends AuthPersistantIF> implements AuthMessageServiceIF {
  public static final String CHALLENGE = "challenge";
  private final AuthPersistantServiceIF<T, U> authPersistantServiceIF;
  private final ClientResponseService okResponseService;
  private final String superconductorRelayUrl;

  public AuthMessageService(
      @NonNull AuthPersistantServiceIF<T, U> authPersistantServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl) {
    this.authPersistantServiceIF = authPersistantServiceIF;
    this.okResponseService = okResponseService;
    this.superconductorRelayUrl = superconductorRelayUrl;
  }

  public void processIncoming(@NonNull CanonicalAuthenticationMessage authMessage, @NonNull String sessionId) {
    log.debug("{} processing incoming AUTH message: [{}]", getClass().getSimpleName(), authMessage);
    log.debug("AUTH message sessionId: {}", sessionId);
    String challenge = getChallenge(authMessage);
//    TODO: check non-blank / quality password /etc
    log.debug("AUTH message challenge string: {}, matched", challenge);

    String relayUriString = getRelay(authMessage);
    PublicKey pubKey = authMessage.getEvent().getPublicKey();
    if (!relayUriString.equalsIgnoreCase(superconductorRelayUrl)) {
      log.debug("AUTH message failed, relay URI string: [{}]", relayUriString);
      sendAuthFailed(authMessage, sessionId,
          String.format("restricted: provided authentication relay URI [%s] does not match this relay host's URI [%s]", relayUriString, superconductorRelayUrl)
      );
      return;
    }

    Long createdAt = Instant.now().toEpochMilli();
    authPersistantServiceIF.save(
        sessionId,
        pubKey,
        challenge,
        createdAt);
    log.debug("auth saved for pubkey [{}], session [{}], createdAt [{}]", pubKey, sessionId, createdAt);

    okResponseService.processOkClientResponse(
        sessionId,
        new EventMessage(authMessage.getEvent(), sessionId),
        String.format("success: auth saved for pubkey [%s], session [%s], created at [%s]", pubKey, sessionId, createdAt));
  }

  @Override
  public Command getCommand() {
    return Command.AUTH;
  }

  private void sendAuthFailed(CanonicalAuthenticationMessage authMessage, String sessionId, String failureReason) {
    okResponseService.processNotOkClientResponse(sessionId, new EventMessage(authMessage.getEvent(), sessionId), failureReason);
  }

  private String getChallenge(CanonicalAuthenticationMessage authMessage) {
    return Filterable.getTypeSpecificTags(GenericTag.class, authMessage.getEvent()).stream()
        .filter(tag ->
            tag.getCode().equalsIgnoreCase(CHALLENGE))
        .map(GenericTag::getAttributes)
        .toList().getFirst().getFirst().getValue().toString();
  }

  private String getRelay(CanonicalAuthenticationMessage authMessage) {
    return Filterable.getTypeSpecificTags(RelayTag.class, authMessage.getEvent())
        .stream()
        .findFirst()
        .map(RelayTag::getRelay).orElseThrow()
        .getUri().toString();
  }
}
