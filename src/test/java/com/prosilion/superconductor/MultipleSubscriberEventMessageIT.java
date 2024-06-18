package com.prosilion.superconductor;

import kotlin.jvm.Synchronized;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.HexFormat;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
class MultipleSubscriberEventMessageIT {
  public final static String globalEventJson = "[\"EVENT\",{\"id\":\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\",\"kind\":1,\"content\":\"1111111111\",\"pubkey\":\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";

  private static final String TARGET_CONTENT = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";
  private static final String SCHEME_WS = "ws";
  private static final String HOST = "localhost";
  private static final String PORT = "5555";
  private static final String WEBSOCKET_URL = SCHEME_WS + "://" + HOST + ":" + PORT;

  private WebSocketStompClient eventStompClient;

  private final Integer TARGET_COUNT = 100;
  private final AtomicReference<Integer> resultCount = new AtomicReference<>(0);


  @BeforeAll
  public void setup() {
    HexGenerator hexGenerator = new HexGenerator(32);
    IntStream.range(0, TARGET_COUNT).parallel().forEach(_ -> {
      final WebSocketStompClient reqStompClient = WebSocketStompClientFactory.getInstance();
      CompletableFuture<WebSocketSession> reqExecute = reqStompClient
          .getWebSocketClient().execute(
              new ReqMessageSocketHandler(
                  hexGenerator.generateRandomHexString()),
              WEBSOCKET_URL, "");
      await().until(reqExecute::isDone);
      reqStompClient.start();
    });

    eventStompClient = WebSocketStompClientFactory.getInstance();
    CompletableFuture<WebSocketSession> eventExecute = eventStompClient.getWebSocketClient().execute(new EventMessageSocketHandler(), WEBSOCKET_URL, "");
    await().until(eventExecute::isDone);
  }

  @Test
  void testEventMessageThenReqMessage() {
    assertDoesNotThrow(() -> eventStompClient.start());
    assertDoesNotThrow(() -> eventStompClient.stop());

    await().untilAtomic(resultCount, equalTo(TARGET_COUNT));

    System.out.println("-------------------");
    System.out.println("-------------------");
    System.out.printf("success count [%s] of target count [%s]%n", resultCount, TARGET_COUNT);
    System.out.println("-------------------");
    System.out.println("-------------------");
  }

  private static class EventMessageSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      session.sendMessage(new TextMessage(globalEventJson));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      assertTrue(message.getPayload().toString().contains(TARGET_CONTENT));
      session.close();
    }
  }

  private class ReqMessageSocketHandler extends TextWebSocketHandler {
    private final String reqJson;

    public ReqMessageSocketHandler(String subId) {
      reqJson = "[\"REQ\",\"" + subId + "\",{\"ids\":[\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      session.sendMessage(new TextMessage(reqJson));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      assertEquals(globalEventJson, message.getPayload().toString());
      incrementRange();
      session.close();
    }
  }

  @Synchronized
  private void incrementRange() {
    resultCount.getAndSet(resultCount.get() + 1);
  }

  private static class HexGenerator {
    private int hexSeed;

    public HexGenerator(int size) {
      StringBuilder hexStringBuilder = null;
      for (int i = 0; i < size; i++) {
//        below starts with all random digits
        int randomNumber = new Random().nextInt(16);
        char hexDigit = (randomNumber < 10) ? (char) ('0' + randomNumber) : (char) ('a' + (randomNumber - 10));
        hexStringBuilder = new StringBuilder();
        hexStringBuilder.append(hexDigit);
      }
      hexSeed = HexFormat.fromHexDigits(Objects.requireNonNull(hexStringBuilder));
    }

    public String generateRandomHexString() {
      return Integer.toHexString(hexSeed++);
    }
  }

  static class WebSocketStompClientFactory {
    private static final MappingJackson2MessageConverter mappingJackson2MessageConverter = new MappingJackson2MessageConverter();
    static WebSocketStompClient getInstance() {
      WebSocketStompClient webSocketStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      webSocketStompClient.setMessageConverter(mappingJackson2MessageConverter);
      return webSocketStompClient;
    }
  }
}