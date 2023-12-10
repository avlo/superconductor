package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.service.BaseMessageDecoderWrapper;
import com.prosilion.nostrrelay.service.BaseMessageEncoderWrapper;
import com.prosilion.nostrrelay.service.EventService;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.java.Log;
import nostr.event.BaseEvent;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseEventEncoder;
import nostr.event.message.EventMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;

@ServerEndpoint(
    value = "/"
    , decoders = BaseMessageDecoderWrapper.class
    , encoders = BaseMessageEncoderWrapper.class
)
@Log
public class NostrEventController {
  private Session session;
  private final static Set<NostrEventController> chatEndpoints = new CopyOnWriteArraySet<>();
  private final EventService eventService;

  public NostrEventController(EventService eventService) {
    this.eventService = eventService;
  }

  @OnOpen
  public void onOpen(Session session) {
    log.log(Level.INFO, "NostrEventController @OnOpen from session: {0}", new Object[]{session});
    this.session = session;
    chatEndpoints.add(this);
  }

  @OnMessage
  public void onMessage(Session session, BaseMessage message) {
    log.log(Level.INFO, "NostrEventController @OnMessage: {0}\nFrom session: {1}\n", new Object[]{message, session});
    broadcast(eventService.processIncoming(message));
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

  private static void broadcast(BaseMessage message) {
    chatEndpoints.forEach(endpoint -> {
      synchronized (endpoint) {
        try {
          log.log(Level.INFO, "33333333333333333333333333333333333333");
          log.log(Level.INFO, "33333333333333333333333333333333333333");
          endpoint.session.getBasicRemote().sendObject(message);
          log.log(Level.INFO, new BaseEventEncoder((BaseEvent) ((EventMessage) message).getEvent()).encode());
          log.log(Level.INFO, "33333333333333333333333333333333333333");
          log.log(Level.INFO, "33333333333333333333333333333333333333");
        } catch (IOException | EncodeException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
