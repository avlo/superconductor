package com.prosilion.nostrrelay.controller;

import com.prosilion.nostrrelay.model.Message;
import com.prosilion.nostrrelay.service.MessageDecoder;
import com.prosilion.nostrrelay.service.MessageEncoder;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint(
    value = "/{username}",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class
)
@Component
public class EventController {
  private Session session;
  private static Set<EventController> chatEndpoints = new CopyOnWriteArraySet<>();
  private static HashMap<String, String> users = new HashMap<>();

  @OnOpen
  public void onOpen(Session session, @PathParam("username") String username) {
    System.out.println("username: " + username);
    this.session = session;
    chatEndpoints.add(this);
    users.put(session.getId(), username);

    Message message = new Message();
    message.setFrom(username);
    message.setContent("Connected!");
    broadcast(message);
  }

  @OnMessage
  public void onMessage(Session session, Message message) {
    message.setFrom(users.get(session.getId()));
    broadcast(message);
  }

  @OnClose
  public void onClose(Session session) {
    chatEndpoints.remove(this);
    Message message = new Message();
    message.setFrom(users.get(session.getId()));
    message.setContent("Disconnected!");
    broadcast(message);
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    // Do error handling here
  }

  private static void broadcast(Message message) {
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
