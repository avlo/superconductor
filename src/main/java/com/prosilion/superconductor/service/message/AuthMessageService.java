package com.prosilion.superconductor.service.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.superconductor.entity.auth.AuthEntity;
import com.prosilion.superconductor.service.event.AuthEntityService;
import com.prosilion.superconductor.service.okresponse.ClientOkResponseService;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.ElementAttribute;
import nostr.event.impl.GenericTag;
import nostr.event.message.CanonicalAuthenticationMessage;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AuthMessageService<T extends CanonicalAuthenticationMessage> implements MessageService<T> {
  @Getter
  public final String command = "AUTH";
  private final AuthEntityService authEntityService;
  private final ClientOkResponseService okResponseService;

  @Autowired
  public AuthMessageService(
      AuthEntityService authEntityService,
      ClientOkResponseService okResponseService) {
    this.authEntityService = authEntityService;
    this.okResponseService = okResponseService;
  }

  public void processIncoming(@NotNull T authMessage, @NonNull String sessionId) {
    log.info("processing incoming AUTH message: [{}]", authMessage);
    log.info("AUTH message NIP: {}", authMessage.getNip());
    log.info("AUTH message sessionId: {}", sessionId);
    try {
      String challengeValue = getElementValue(authMessage, "challenge");
//    TODO: check non-blank / quality password /etc
      log.info("AUTH message challenge string: {}, matched", challengeValue);

      String relayUriString = getElementValue(authMessage, "relay");
      if (!relayUriString.equalsIgnoreCase("ws://localhost:5555")) {
        log.info("AUTH message failed, relay URI string: [{}]", relayUriString);
        okResponseService.processNotOkClientResponse(
            sessionId,
            new EventMessage(authMessage.getEvent()),
            String.format("authentication relay URI [%s] does not match relay host URI", relayUriString));
      }

      authEntityService.save(
          new AuthEntity(
              authMessage.getEvent().getPubKey().toString(),
              challengeValue,
              sessionId));
      log.info("successful saved AUTH for PubKey {}, session {}", authMessage.getEvent().getPubKey().toString(), sessionId);

      okResponseService.processOkClientResponse(
          sessionId,
          new EventMessage(authMessage.getEvent()));
    } catch (JsonProcessingException e) {
      log.info("FAILED auth message json: {}", e.getMessage());
    }
  }

  private String getElementValue(CanonicalAuthenticationMessage authMessage, String targetString) {
    List<GenericTag> genericTags = authMessage.getEvent().getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast).toList();

    String challenge = genericTags.stream()
        .filter(tag -> tag.getCode().equalsIgnoreCase(targetString)).map(GenericTag::getAttributes).toList().get(0).get(0).getValue().toString();
    return challenge;
  }
}