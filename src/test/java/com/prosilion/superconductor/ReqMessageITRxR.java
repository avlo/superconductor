package com.prosilion.superconductor;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.Executors;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    webEnvironment = WebEnvironment.DEFINED_PORT,
    classes = SuperConductorApplication.class)
@Sql(scripts = {"/bulk_data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD) // class level @Sql
@DirtiesContext
class ReqMessageITRxR {
  private final static String SCHEME_WS = "ws";
  private final static String HOST = "localhost";
  private final static String PORT = "5555";
  public final static String WEBSOCKET_URL = SCHEME_WS + "://" + HOST + ":" + PORT;

  WebSocketStompClient reqStompClient;
  ReqMessageSocketHandler reqTextHandler;

  @BeforeAll
  public void setup() {
    reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
    reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());

    reqTextHandler = new ReqMessageSocketHandler();
  }

  @Test
  void testEventMessageThenReqMessage() {
    System.out.println("****************");
    System.out.println("****************");

    try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {

      var future1 = executor.submit(() -> reqStompClient.getWebSocketClient().execute(reqTextHandler, WEBSOCKET_URL, ""));
      var future2 = executor.submit(() -> reqStompClient.start());

      System.out.println("awaiting events....");
      System.out.println("****************");
      System.out.println("****************");
    }
//    new Scanner(System.in).nextLine();
  }

  class ReqMessageSocketHandler extends TextWebSocketHandler {
    private final static String reqJson = "[\"REQ\",\"16b8772f38bbdee735445862065f34adad5d1522b08203d02b1d1b207a798c9a\",{\"ids\":[\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      System.out.println("1111111111111111111");
      System.out.println("1111111111111111111");
      System.out.println("server connection established");
      System.out.println("sending req message : " + reqJson);
      session.sendMessage(new TextMessage(reqJson));
      System.out.println("req message sent");
      System.out.println("1111111111111111111");
      System.out.println("1111111111111111111");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      System.out.println("2222222222222222222");
      System.out.println("2222222222222222222");
      System.out.println("req handleMessage() event received with payload:");
      Object payload = message.getPayload();
      System.out.println("\n" + payload);
      System.out.println("2222222222222222222");
      System.out.println("2222222222222222222");
    }
  }
}