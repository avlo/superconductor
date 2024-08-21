package com.prosilion.superconductor.controller;

import com.prosilion.superconductor.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.service.message.CloseMessageServiceIF;
import com.prosilion.superconductor.service.message.MessageService;
import com.prosilion.superconductor.service.message.RelayInfoDocService;
import com.prosilion.superconductor.service.okresponse.CloseClientResponse;
import com.prosilion.superconductor.service.okresponse.OkClientResponse;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.CloseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Controller
@EnableWebSocket
public class NostrEventController<T extends BaseMessage, U extends CloseMessage> extends TextWebSocketHandler implements WebSocketConfigurer {
  private final Map<String, MessageService<T>> messageServiceMap;
  private final CloseMessageServiceIF<U> closeMessageService;
  private final RelayInfoDocService relayInfoDocService;
  private final Map<String, WebSocketSession> mapSessions = new HashMap<>();

  @Autowired
  public NostrEventController(
      List<MessageService<T>> messageServices,
      CloseMessageServiceIF<U> closeMessageService,
      RelayInfoDocService relayInfoDocService) {
    this.messageServiceMap = messageServices.stream().collect(Collectors.toMap(MessageService<T>::getCommand, Function.identity()));
    this.closeMessageService = closeMessageService;
    this.relayInfoDocService = relayInfoDocService;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(this, "/")
        .setHandshakeHandler(
            new DefaultHandshakeHandler(
                new TomcatRequestUpgradeStrategy()));
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws IOException {
    log.info("Connected new session [{}]", session.getId());
    if (RelayInfoDocService.isRelayInformationDocumentRequest(session)) {
      session.sendMessage(relayInfoDocService.processIncoming());
      session.close();
      return;
    }
    mapSessions.put(session.getId(), session);
  }

  /**
   * Handles WebSocket close event.  Differentiated from Nostr Close Event in method
   * {@link #handleTextMessage(WebSocketSession, TextMessage) }
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    log.info("Closing session [{}]...", session.getId());
    try {
      closeMessageService.removeSubscriberBySessionId(session.getId());
      Objects.requireNonNull(mapSessions.remove(session.getId()));
      log.info("Subscriber session closed.");
    } catch (NullPointerException e) {
      closeMessageService.closeSession(session.getId());
      log.info("Non-Subscriber session closed.");
    }
  }

  /**
   * Nostr Event Handlers
   * note: Nostr CLOSE event is differentiated from WebSocket close event in method
   * {@link #afterConnectionClosed(WebSocketSession, CloseStatus) }
   */
  @Override
  public void handleTextMessage(@NotNull WebSocketSession session, TextMessage baseMessage) {
    log.info("Message from session [{}]", session.getId());
    T message = (T) new BaseMessageDecoder<>().decode(baseMessage.getPayload());
    messageServiceMap.get(message.getCommand()).processIncoming(message, session.getId());
  }

  @EventListener
  public <U extends BaseMessage> void broadcastMessageEvent(BroadcastMessageEvent<U> message) {
    TextMessage textMessage = message.getMessage();
    String sessionId = message.getSessionId();
    log.info("NostrEventController broadcast to\nsession:\n\t{}\nmessage:\n\t{}", sessionId, textMessage.getPayload());
    broadcast(sessionId, textMessage);
  }

  @EventListener
  public void broadcastOkClientResponse(OkClientResponse message) {
    final TextMessage okResponseMessage = message.getOkResponseMessage();
    final String sessionId = message.getSessionId();
    log.info("NostrEventController OK response to\nclient:\n\t{}\nresponse:\n\t{}", sessionId, okResponseMessage.getPayload());
    broadcast(sessionId, okResponseMessage);
    if (!message.isValid()) {
      afterConnectionClosed(Objects.requireNonNull(mapSessions.remove(sessionId)), CloseStatus.POLICY_VIOLATION);
    }
  }

  @EventListener
  public void broadcastCloseClientResponse(CloseClientResponse message) {
    final TextMessage okResponseMessage = message.getCloseResponseMessage();
    final String sessionId = message.getSessionId();
    log.info("NostrEventController CLOSE response to\nsessionId:\n\t{}\npayload:\n\t{}", sessionId, okResponseMessage.getPayload());
    broadcast(sessionId, okResponseMessage);
  }

  private void broadcast(String sessionId, TextMessage message) {
    try {
      mapSessions.get(sessionId).sendMessage(message);
    } catch (IOException e) {
      log.info("Orphaned client session [{}], message [{}] not sent", sessionId, message);
    }
  }
}
