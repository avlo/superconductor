package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.service.MessageDecoderDecorator;
import com.prosilion.nostrrelay.service.MessageEncoderDecorator;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.java.Log;
import nostr.base.ChannelProfile;
import nostr.base.PublicKey;
import nostr.event.BaseMessage;
import nostr.event.impl.ChannelCreateEvent;
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
    value = "/{username}",
    decoders = MessageDecoderDecorator.class,
    encoders = MessageEncoderDecorator.class
)
@Component
@Log
public class NostrEventController {
  private static final Identity SENDER = Identity.generateRandomIdentity();
  private Session session;
  private static Set<NostrEventController> chatEndpoints = new CopyOnWriteArraySet<>();
  private static HashMap<String, String> users = new HashMap<>();

  @OnOpen
  public void onOpen(Session session, @PathParam("username") String username) throws MalformedURLException, URISyntaxException {
    System.out.println("username: " + username);
    this.session = session;
    chatEndpoints.add(this);
    users.put(session.getId(), username);

    final PublicKey publicKeySender = SENDER.getPublicKey();
    var channel = new ChannelProfile("JNostr Channel", "This is a channel to test NIP28 in nostr-java", "https://cdn.pixabay.com/photo/2020/05/19/13/48/cartoon-5190942_960_720.jpg");
    var event = new ChannelCreateEvent(publicKeySender, channel);

    SENDER.sign(event);
    BaseMessage message = new EventMessage(event);
    broadcast(message);
  }

  @OnMessage
  public void onMessage(Session session, BaseMessage message) {

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
          endpoint.session.getBasicRemote().
              sendObject(message);
        } catch (IOException | EncodeException e) {
          e.printStackTrace();
        }
      }
    });
  }
}
