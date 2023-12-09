package com.prosilion.nostrrelay.controller;


import com.google.gson.Gson;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {

  List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  @Override
  public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    System.out.println("111111111111111111111");
    System.out.println("111111111111111111111");
    for(WebSocketSession webSocketSession : sessions) {
      Map value = new Gson().fromJson(message.getPayload(), Map.class);
      WebSocketMessage m = new TextMessage("server received form NAME value: " + value.get("name"));
      System.out.println(String.format("payload length [%s]", m.getPayloadLength()));
      System.out.println(String.format("is last? [%s]", m.isLast()));
      System.out.println(String.format("payload.getPayload() [%s]", m.getPayload()));
      System.out.println(String.format("payload.toString() [%s]", m));
      webSocketSession.sendMessage(m);
      System.out.println("------------------------------------");
    }
    System.out.println("111111111111111111111");
    System.out.println("111111111111111111111");
  }

  @Override
  public void afterConnectionEstablished(WebSocketSession session) {
    //the messages will be broadcasted to all users.
    System.out.println("22222222222222222222222");
    System.out.println("22222222222222222222222");
    sessions.add(session);
    System.out.println("22222222222222222222222");
    System.out.println("22222222222222222222222");
  }
}