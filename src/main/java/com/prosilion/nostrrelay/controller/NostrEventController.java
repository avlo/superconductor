package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.service.MessageGeneric;
import com.prosilion.nostrrelay.util.BaseMessageDecoderWrapper;
import com.prosilion.nostrrelay.util.BaseMessageEncoderWrapper;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.java.Log;
import nostr.event.BaseEvent;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseEventEncoder;
import nostr.event.message.EventMessage;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.logging.Level;

@ServerEndpoint(
    value = "/"
    , decoders = BaseMessageDecoderWrapper.class
    , encoders = BaseMessageEncoderWrapper.class
)
@Controller
@Log
public class NostrEventController {
  private Session session;

  @OnOpen
  public void onOpen(Session session) {
    log.log(Level.INFO, "NostrEventController @OnOpen from session: {0}", new Object[]{session});
    this.session = session;
  }

  @OnMessage
  public void onMessage(Session session, MessageGeneric messageGeneric) {
    log.log(Level.INFO, "NostrEventController @OnMessage: {0}\nFrom session: {1}\n", new Object[]{messageGeneric, session});
    broadcast(messageGeneric.processIncoming());
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

  private void broadcast(BaseMessage message) {
    try {
      log.log(Level.INFO, "NostrEventController broadcast: {0}", message.getCommand());
      session.getBasicRemote().sendObject(message);
      log.log(Level.INFO, new BaseEventEncoder((BaseEvent) ((EventMessage) message).getEvent()).encode());
    } catch (IOException | EncodeException e) {
      e.printStackTrace();
    }
  }
}
