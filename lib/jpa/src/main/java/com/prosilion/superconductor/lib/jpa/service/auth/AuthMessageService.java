package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.message.CanonicalAuthenticationMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.message.MessageServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public class AuthMessageService implements MessageServiceIF<CanonicalAuthenticationMessage> {
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

  public void processIncoming(@NonNull CanonicalAuthenticationMessage authMessage, @NonNull String sessionId) {
    log.debug("processing incoming AUTH message: [{}]", authMessage);
    log.debug("AUTH message sessionId: {}", sessionId);
    String challengeValue = getElementValue(authMessage, "challenge");
//    TODO: check non-blank / quality password /etc
    log.debug("AUTH message challenge string: {}, matched", challengeValue);

    String relayUriString = getElementValue(authMessage, "relay");
    String pubKey = authMessage.getEvent().getPublicKey().toString();
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
