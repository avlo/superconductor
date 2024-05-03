package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.pubsub.BroadcastMessageEvent;
import com.prosilion.nostrrelay.service.message.CloseMessageService;
import com.prosilion.nostrrelay.service.message.MessageService;
import jakarta.persistence.NoResultException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.java.Log;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseMessageDecoder;
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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Log
@Controller
@EnableWebSocket
public class NostrEventController<T extends BaseMessage> extends TextWebSocketHandler implements WebSocketConfigurer {
  private final Map<String, MessageService<T>> messageServiceMap;
  private final CloseMessageService closeMessageService;
  private final Map<String, WebSocketSession> mapSessions = new HashMap<>();

  @Autowired
  public NostrEventController(List<MessageService<T>> messageServices, CloseMessageService closeMessageService) {
    this.messageServiceMap = messageServices.stream().collect(Collectors.toMap(MessageService<T>::getCommand, Function.identity()));
    this.closeMessageService = closeMessageService;
  }

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry.addHandler(this, "/");
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    log.info(String.format("Connected new session [%s]", session.getId()));
    mapSessions.put(session.getId(), session);
  }

  /**
   * Handles WebSocket close event.  Differentiated from Nostr Close Event in method
   * {@link #handleTextMessage(WebSocketSession, TextMessage) }
   */
  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    log.info(String.format("Closing session [%s]...", session.getId()));
    try {
//      NIP specs somewhat ambiguous whether to de-activate session or remove it.  for now, do remove
//      closeMessageService.deactivateSubscriberBySessionId(session.getId());
      closeMessageService.removeSubscriberBySessionId(session.getId());
      log.info("Subscriber session closed.");
    } catch (NoResultException e) {
      log.info("Non-Subscriber session closed.");
    }
    mapSessions.remove(session.getId());
  }

  /**
   * Nostr Event Handlers
   * note: Nostr CLOSE event is differentiated from WebSocket close event in method
   * {@link #afterConnectionClosed(WebSocketSession, CloseStatus) }
   */
  @Override
  public void handleTextMessage(@NotNull WebSocketSession session, TextMessage baseMessage) {
    log.info(String.format("Message from session [%s]", session.getId()));
    T message = (T) new BaseMessageDecoder(baseMessage.getPayload()).decode();
    messageServiceMap.get(message.getCommand()).processIncoming(message, session.getId());
  }

  //  @Async
  @EventListener
  public <U extends BaseMessage> void broadcast(BroadcastMessageEvent<U> message) throws IOException {
    log.log(Level.INFO, String.format("NostrEventController broadcast to%nsession:%n\t%s%nmessage:%n\t%s%n", message.getSessionId(), message.getMessage().getPayload()));
    mapSessions.get(message.getSessionId()).sendMessage(message.getMessage());
  }
}
