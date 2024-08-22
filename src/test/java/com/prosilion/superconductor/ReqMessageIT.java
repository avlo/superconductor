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

import static org.awaitility.Awaitility.given;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Sql(scripts = {"/event.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_CLASS) // class level @Sql
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
class ReqMessageIT {
  public final static String globalEventJson = "[\"EVENT\",\"16b8772f38bbdee735445862065f34adad5d1522b08203d02b1d1b207a798c9a\",{\"id\":\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\",\"kind\":1,\"content\":\"1111111111\",\"pubkey\":\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";

  private static final String SCHEME_WS = "ws";
  private static final String HOST = "localhost";
  private static final String PORT = "5555";
  private static final String WEBSOCKET_URL = SCHEME_WS + "://" + HOST + ":" + PORT;

  private WebSocketStompClient reqStompClient;

  @BeforeAll
  public void setup() {
    reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
    reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    reqStompClient.getWebSocketClient().execute(new ReqMessageSocketHandler(), WEBSOCKET_URL, "");
  }

  @Test
  void testEventMessageThenReqMessage() {
    assertDoesNotThrow(() -> reqStompClient.start());
    assertDoesNotThrow(() -> reqStompClient.stop());

    given().ignoreException(InterruptedException.class)
        .await().until(() -> !reqStompClient.isRunning());
  }

  static class ReqMessageSocketHandler extends TextWebSocketHandler {
    private final static String reqJson = "[\"REQ\",\"16b8772f38bbdee735445862065f34adad5d1522b08203d02b1d1b207a798c9a\",{\"ids\":[\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      session.sendMessage(new TextMessage(reqJson));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      assertEquals(globalEventJson, message.getPayload().toString());
      session.close();
    }
  }
}