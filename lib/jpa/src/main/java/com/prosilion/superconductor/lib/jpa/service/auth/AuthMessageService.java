package com.prosilion.superconductor.lib.jpa.service.auth;

import com.prosilion.nostr.enums.Command;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.message.CanonicalAuthenticationMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.service.clientresponse.ClientResponseService;
import com.prosilion.superconductor.base.service.message.MessageServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.auth.AuthEntity;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnExpression("${superconductor.auth.req.active:true} || ${superconductor.auth.event.active:true}")
public class AuthMessageService implements MessageServiceIF<CanonicalAuthenticationMessage> {
  public static final String CHALLENGE = "challenge";
  private final AuthEntityServiceIF authEntityService;
  private final ClientResponseService okResponseService;
  private final String superconductorRelayUrl;

  @Autowired
  public AuthMessageService(
      @NonNull AuthEntityServiceIF authEntityServiceIF,
      @NonNull ClientResponseService okResponseService,
      @NonNull @Value("${superconductor.relay.url}") String superconductorRelayUrl) {
    this.authEntityService = authEntityServiceIF;
    this.okResponseService = okResponseService;
    this.superconductorRelayUrl = superconductorRelayUrl;
  }

  public void processIncoming(@NonNull CanonicalAuthenticationMessage authMessage, @NonNull String sessionId) {
    log.debug("{} processing incoming AUTH message: [{}]", getClass().getSimpleName(), authMessage);
    log.debug("AUTH message sessionId: {}", sessionId);
    String challengeValue = getChallenge(authMessage);
//    TODO: check non-blank / quality password /etc
    log.debug("AUTH message challenge string: {}, matched", challengeValue);

    String relayUriString = getRelay(authMessage);
    String pubKey = authMessage.getEvent().getPublicKey().toString();
    if (!relayUriString.equalsIgnoreCase(superconductorRelayUrl)) {
      log.debug("AUTH message failed, relay URI string: [{}]", relayUriString);
      sendAuthFailed(authMessage, sessionId,
          String.format("restricted: provided authentication relay URI [%s] does not match this relay host's URI [%s]", relayUriString, superconductorRelayUrl)
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
