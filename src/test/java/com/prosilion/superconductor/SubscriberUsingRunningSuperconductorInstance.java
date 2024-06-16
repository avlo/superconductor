package com.prosilion.superconductor;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Slf4j
/**
 * two modes:
 * mode 1) register here first, then generate superconductor event:
 *   1) start superconductor
 *   2) start this class
 *    2a) superconductor console should display the client in this test as connected
 *    2b) afterConnectionEstablished() method in this class should show connection confirmation
 *   3) open browser to 01.html
 *   4) leave browser fields as is (i.e., send message field should have "11111111"
 *   5) click browser "send"
 *    5a) superconductor console should show sent message
 *    5b) handleMessage() in this class should show message retrieval confirmation
 *
 * mode 2) generate superconductor event first
 *   1) open browser to 01.html
 *     1a) leave browser fields as is (i.e., send message field should have "11111111"
 *     2a) click browser "send"
 *   2) start this class
 *     2a) superconductor console should show sent message
 *     2b) handleMessage() in this class should show message retrieval confirmation
 *
 * note: in both cases, clicking "send" again in browser should register events here
 */
//@SpringBootTest
class SubscriberUsingRunningSuperconductorInstance {
  private final static String requestJson = "[\"REQ\",\"16b8772f38bbdee735445862065f34adad5d1522b08203d02b1d1b207a798c9a\",{\"ids\":[\"ba748cd29396fbbe613e6bc0e2472cd60e6815c62bc2b4db8d39e4b27f75f1a6\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";

  private final static String SCHEME_WS = "ws";
  private final static String HOST = "localhost";
  private final static String PORT = "5555";
  private final static String WEBSOCKET_URL = SCHEME_WS + "://" + HOST + ":" + PORT;

  WebSocketStompClient stompClient;
  TextWebSocketHandler sessionHandler;

  @BeforeEach
  public void setup() {
    WebSocketClient webSocketClient = new StandardWebSocketClient();
    stompClient = new WebSocketStompClient(webSocketClient);
    stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    sessionHandler = new LocalWebSocketHandler();
  }

  @Test
  void shouldReceiveAMessageFromTheServer() {
    assertDoesNotThrow(() -> {
      stompClient.getWebSocketClient().execute(sessionHandler, WEBSOCKET_URL, requestJson);
      stompClient.start();
    });
    new Scanner(System.in).nextLine(); // Don't close immediately.
  }

  class LocalWebSocketHandler extends TextWebSocketHandler {

    @Override
    public void afterConnectionEstablished(org.springframework.web.socket.WebSocketSession session) throws Exception {
      log.info("server connection established");
      log.info("sending message : " + requestJson);
      session.sendMessage(new TextMessage(requestJson));
      log.info("message sent");
    }

    @Override
    public void handleMessage(org.springframework.web.socket.WebSocketSession session, org.springframework.web.socket.WebSocketMessage<?> message) throws Exception {
      log.info("handleMessage() event received with payload:");
      Object payload = message.getPayload();
      log.info("\n" + payload);
    }
  }
}