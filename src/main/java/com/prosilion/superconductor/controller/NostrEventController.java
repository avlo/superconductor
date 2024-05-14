package com.prosilion.superconductor.controller;

import com.prosilion.superconductor.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.service.message.CloseMessageService;
import com.prosilion.superconductor.service.message.MessageService;
import com.prosilion.superconductor.service.message.RelayInfoDocService;
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
public class NostrEventController<T extends BaseMessage> extends TextWebSocketHandler implements WebSocketConfigurer {
  private final Map<String, MessageService<T>> messageServiceMap;
  private final CloseMessageService<CloseMessage> closeMessageService;
  private final RelayInfoDocService relayInfoDocService;
  private final Map<String, WebSocketSession> mapSessions = new HashMap<>();

  @Autowired
  public NostrEventController(
      List<MessageService<T>> messageServices,
      CloseMessageService<CloseMessage> closeMessageService,
      RelayInfoDocService relayInfoDocService) {
    this.messageServiceMap = messageServices.stream().collect(Collectors.toMap(MessageService<T>::getCommand, Function.identity()));
    this.closeMessageService = closeMessageService;
    this.relayInfoDocService = relayInfoDocService;
  }

  @Override
  public void registerWebSocketHandlers(
      WebSocketHandlerRegistry registry) {
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
    T message = (T) new BaseMessageDecoder(baseMessage.getPayload()).decode();
    messageServiceMap.get(message.getCommand()).processIncoming(message, session.getId());
  }

  @EventListener
  public <U extends BaseMessage> void broadcast(BroadcastMessageEvent<U> message) throws IOException {
    log.info("NostrEventController broadcast to\nsession:\n\t{}\nmessage:\n\t{}", message.getSessionId(), message.getMessage().getPayload());
    mapSessions.get(message.getSessionId()).sendMessage(message.getMessage());
  }

  @EventListener
  public void broadcast(OkClientResponse message) throws IOException {
    log.info("NostrEventController OK response to\nclient:\n\t{}\nresponse:\n\t{}", message.getSessionId(), message.getOkResponseMessage().getPayload());
    mapSessions.get(message.getSessionId()).sendMessage(message.getOkResponseMessage());
  }
}
