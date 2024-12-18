package com.prosilion.superconductor.controller;

import com.prosilion.superconductor.service.request.pubsub.BroadcastMessageEvent;
import com.prosilion.superconductor.service.message.MessageService;
import com.prosilion.superconductor.service.message.RelayInfoDocService;
import com.prosilion.superconductor.service.clientresponse.ClientResponse;
import com.prosilion.superconductor.service.request.pubsub.TerminatedSocket;
import lombok.extern.slf4j.Slf4j;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Controller
@EnableWebSocket
public class NostrEventController<T extends BaseMessage> extends TextWebSocketHandler implements WebSocketConfigurer {
  private final Map<String, MessageService<T>> messageServiceMap;
  private final RelayInfoDocService relayInfoDocService;
  private final Map<String, WebSocketSession> mapSessions = new HashMap<>();
  private final ApplicationEventPublisher publisher;

  @Value("${superconductor.auth.active}")
  private boolean authActive;

  @Autowired
  public NostrEventController(
      List<MessageService<T>> messageServices,
      RelayInfoDocService relayInfoDocService,
      ApplicationEventPublisher publisher) {
    this.messageServiceMap = messageServices.stream().collect(
        Collectors.toMap(
            MessageService<T>::getCommand,
            Function.identity()));
    this.relayInfoDocService = relayInfoDocService;
    this.publisher = publisher;
  }

  @GetMapping("/api-tests.html")
  public String apiTests(Model model) {
    model.addAttribute("authActive", authActive);
    return "thymeleaf/api-tests";
  }

  @GetMapping("/request-test.html")
  public String requestTest(Model model) {
    model.addAttribute("authActive", authActive);
    return "thymeleaf/request-test";
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
    if (isRelayInformationDocumentRequest(session))
      return;
    mapSessions.put(session.getId(), session);
  }

  /**
   * WebSocket-Termination handler.  Differentiated from Nostr-Close if/when encountered in method
   * {@link #handleTextMessage(WebSocketSession, TextMessage) }
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    log.info("client initiated close, sessionId [{}]", session.getId());
    publisher.publishEvent(new TerminatedSocket(session.getId()));
    closeSession(session);
  }

  /**
   * Nostr-Event/Req/Close Handler.
   * note: Nostr-Close is differentiated from WebSocket-Close if/when encountered in method
   * {@link #afterConnectionClosed(WebSocketSession, CloseStatus) }
   */
  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage baseMessage) {
    log.info("Message from session [{}]", session.getId());
    log.debug("Message content [{}]", baseMessage.getPayload());
    T message = (T) new BaseMessageDecoder<>().decode(baseMessage.getPayload());
    messageServiceMap.get(message.getCommand()).processIncoming(message, session.getId());
  }

  /**
   * Event-Response to client
   */
  @EventListener
  public void broadcastMessageEvent(BroadcastMessageEvent<BaseMessage> message) {
    TextMessage response = message.getMessage();
    String sessionId = message.getSessionId();
    broadcast(sessionId, response);
    log.info("NostrEventController broadcast to\nsession:\n\t{}\nmessage:\n\t{}", sessionId, response.getPayload());
  }

  /**
   * Ok/Close/Notice-Response to client
   */
  @EventListener
  public void broadcastClientResponse(ClientResponse message) {
    TextMessage response = message.getTextMessage();
    String sessionId = message.getSessionId();
    broadcast(sessionId, response);
    if (message.isValid()) {
      log.info("OK response to\nclient:\n\t{}\npayload:\n\t{}", sessionId, response.getPayload());
      return;
    }
    closeSession(sessionId);
    log.info("CLOSE response to\nclient:\n\t{}\npayload:\n\t{}", sessionId, response.getPayload());
  }

  private void broadcast(String sessionId, TextMessage message) {
    try {
      mapSessions.get(Optional.ofNullable(sessionId).orElseThrow()).sendMessage(message);
    } catch (Exception e) {
      log.info("Orphaned client session [{}], message [{}] not sent", sessionId, message);
    }
  }

  private boolean isRelayInformationDocumentRequest(WebSocketSession session) throws IOException {
    if (!RelayInfoDocService.isRelayInformationDocumentRequest(session))
      return false;
    session.sendMessage(relayInfoDocService.processIncoming());
    closeSession(session);
    return true;
  }

  private void closeSession(WebSocketSession session) {
    closeSession(session.getId());
    try {
      session.close();
    } catch (IOException e) {
      log.info("Non-Subscriber session closed.");
    }
  }

  private void closeSession(String sessionId) {
    try {
      mapSessions.get(Optional.ofNullable(sessionId).orElseThrow()).close();
      Objects.requireNonNull(mapSessions.remove(sessionId));
      log.info("sessionId removed from mapSessions");
    } catch (Exception e) {
      log.info("no sessionId to remove from mapSessions");
    }
  }
}
