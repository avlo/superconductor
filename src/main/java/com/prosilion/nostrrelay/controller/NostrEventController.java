package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.service.message.MessageService;
import com.prosilion.nostrrelay.util.DecodedMessageMarshaller;
import com.prosilion.nostrrelay.util.MessageEncoder;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.java.Log;
import nostr.event.BaseEvent;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseEventEncoder;
import nostr.event.message.EventMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.logging.Level;

@ServerEndpoint(
    value = "/"
    , decoders = DecodedMessageMarshaller.class
    , encoders = MessageEncoder.class
)
@Controller
@Log
public class NostrEventController<T extends BaseMessage> {
  private Session session;

  @OnMessage
  public void onMessage(Session session, @NotNull MessageService<T> messageService) {
    log.log(Level.INFO, "NostrEventController @OnMessage: {0}\nFrom session: {1}\n", new Object[]{messageService, session});
    broadcast(messageService.processIncoming());
  }

  private void broadcast(@NotNull T message) {
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
