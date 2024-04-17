package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.pubsub.FireNostrEvent;
import com.prosilion.nostrrelay.service.message.MessageService;
import com.prosilion.nostrrelay.util.DecodedMessageMarshaller;
import com.prosilion.nostrrelay.util.MessageEncoder;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.java.Log;
import nostr.api.NIP01;
import nostr.event.BaseEvent;
import nostr.event.BaseMessage;
import nostr.event.impl.GenericEvent;
import nostr.event.json.codec.BaseEventEncoder;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

@ServerEndpoint(
    value = "/"
    , decoders = DecodedMessageMarshaller.class
    , encoders = MessageEncoder.class
)
@Controller
@Log
public class NostrEventController<T extends BaseMessage, U extends GenericEvent> {
  private Session session = null;

  @OnMessage
  public void onMessage(Session session, @NotNull MessageService<T> messageService) throws InvocationTargetException, IllegalAccessException {
    log.log(Level.INFO, "NostrEventController @OnMessage: {0}\nFrom session: {1}\n", new Object[]{messageService, session});
    this.session = session;
    messageService.processIncoming(session.getId());
  }

  @EventListener
  public void fireEvent(FireNostrEvent<U> tFireNostrEvent) {
    T message = (T) NIP01.createEventMessage(tFireNostrEvent.getEvent(), String.valueOf(tFireNostrEvent.getSubscriberId()));
    broadcast(message);
  }
  public void broadcast(@NotNull T message) {
    try {
      log.log(Level.INFO, "NostrEventController broadcast: {0}", message.getCommand());
      session.getBasicRemote().sendObject(message);
      EventMessage baseEvent = (EventMessage) message;
      BaseEvent event = (BaseEvent) baseEvent.getEvent();
      log.log(Level.INFO, new BaseEventEncoder(event).encode());
    } catch (IOException | EncodeException e) {
      log.log(Level.SEVERE, e.getMessage());
    }
  }

  @OnOpen
  public void onOpen(Session session) {
    log.log(Level.INFO, "NostrEventController @OnOpen from session: {0}", new Object[]{session});
    this.session = session;
  }

  @OnClose
  public void onClose(Session session) {
//    chatEndpoints.remove(this);
//    Message message = new Message();
//    message.setFrom(users.get(session.getId()));
//    message.setContent("Disconnected!");
//    broadcast(message);
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    // Do error handling here
  }
}
