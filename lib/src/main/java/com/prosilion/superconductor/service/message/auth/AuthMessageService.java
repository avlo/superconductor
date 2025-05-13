package com.prosilion.superconductor.service.message.auth;

import com.prosilion.superconductor.entity.auth.AuthEntity;
import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.service.message.MessageServiceIF;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.tag.GenericTag;
import nostr.event.message.CanonicalAuthenticationMessage;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public class AuthMessageService<T extends CanonicalAuthenticationMessage> implements MessageServiceIF<T> {
  @Getter
  public final String command = "AUTH";
  private final AuthEntityService authEntityService;
  private final ClientResponseService okResponseService;

  @Value("${superconductor.relay.url}")
  private String relayUrl;

  @Autowired
  public AuthMessageService(
      AuthEntityService authEntityService,
      ClientResponseService okResponseService) {
    this.authEntityService = authEntityService;
    this.okResponseService = okResponseService;
  }

  public void processIncoming(@NonNull T authMessage, @NonNull String sessionId) {
    log.debug("processing incoming AUTH message: [{}]", authMessage);
    log.debug("AUTH message NIP: {}", authMessage.getNip());
    log.debug("AUTH message sessionId: {}", sessionId);
    String challengeValue = getElementValue(authMessage, "challenge");
//    TODO: check non-blank / quality password /etc
    log.debug("AUTH message challenge string: {}, matched", challengeValue);

    String relayUriString = getElementValue(authMessage, "relay");
    String pubKey = authMessage.getEvent().getPubKey().toString();
    if (!relayUriString.equalsIgnoreCase(relayUrl)) {
      log.debug("AUTH message failed, relay URI string: [{}]", relayUriString);
      sendAuthFailed(authMessage, sessionId,
          String.format("restricted: provided authentication relay URI [%s] does not match this relay host's URI [%s]", relayUriString, relayUrl)
      );
      return;
    }

    Long createdAt = Instant.now().toEpochMilli();
    authEntityService.save(
        new AuthEntity(
            pubKey,
            challengeValue,
            sessionId,
            createdAt));
    log.debug("auth saved for pubkey [{}], session [{}], createdAt [{}]", pubKey, sessionId, createdAt);

    okResponseService.processOkClientResponse(
        sessionId,
        new EventMessage(authMessage.getEvent()),
        String.format("success: auth saved for pubkey [%s], session [%s], created at [%s]", pubKey, sessionId, createdAt));
  }

  private void sendAuthFailed(T authMessage, String sessionId, String failureReason) {
    okResponseService.processNotOkClientResponse(sessionId, new EventMessage(authMessage.getEvent()), failureReason);
  }

  private String getElementValue(CanonicalAuthenticationMessage authMessage, String attribute) {
    return authMessage.getEvent().getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(tag ->
            tag.getCode().equalsIgnoreCase(attribute))
        .map(GenericTag::getAttributes)
        .toList().get(0).get(0).getValue().toString();
  }
}
