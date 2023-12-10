package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.service.BaseMessageDecoderWrapper;
import com.prosilion.nostrrelay.service.BaseMessageEncoderWrapper;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.java.Log;
import nostr.event.BaseEvent;
import nostr.event.BaseMessage;
import nostr.event.json.codec.BaseEventEncoder;
import nostr.event.json.codec.BaseMessageDecoder;
import nostr.event.message.EventMessage;
import nostr.id.Identity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;

@ServerEndpoint(
    value = "/"
    , decoders = BaseMessageDecoderWrapper.class
    , encoders = BaseMessageEncoderWrapper.class
)
@Component
@Log
public class NostrEventController {
  private static final Identity SENDER = Identity.generateRandomIdentity();
  private Session session;
  private static Set<NostrEventController> chatEndpoints = new CopyOnWriteArraySet<>();
  private static HashMap<String, String> users = new HashMap<>();

  @OnOpen
  public void onOpen(Session session) throws MalformedURLException, URISyntaxException {
    log.log(Level.INFO, "11111111111111111111111111111111111111111");
    log.log(Level.INFO, "11111111111111111111111111111111111111111");
    log.log(Level.INFO, "NostrEventController onOpen from session: {0}", new Object[]{session});
    this.session = session;
    chatEndpoints.add(this);
  }

  @OnMessage
//  public void onMessage(Session session, EventMessage message) {
  public void onMessage(Session session, BaseMessage message) {
    log.log(Level.INFO, "22222222222222222222222222222222222222");
    log.log(Level.INFO, "22222222222222222222222222222222222222");

    log.log(Level.INFO, "Processing message: {0} from session: {1}", new Object[]{message, session});

//    var oMsg = new BaseMessageDecoder(message).decode();
    final String command = message.getCommand();

    switch (command) {
      case "EVENT" -> {
        if (message instanceof EventMessage msg) {
//          var subId = msg.getSubscriptionId();
          broadcast(msg);
        } else {
          throw new AssertionError("EVENT");
        }
      }
      default -> throw new AssertionError("Unknown command " + command);
    }
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
          log.log(Level.INFO, new BaseEventEncoder((BaseEvent) ((EventMessage)message).getEvent()).encode());
          log.log(Level.INFO, "33333333333333333333333333333333333333");
          log.log(Level.INFO, "33333333333333333333333333333333333333");
        } catch (IOException | EncodeException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
