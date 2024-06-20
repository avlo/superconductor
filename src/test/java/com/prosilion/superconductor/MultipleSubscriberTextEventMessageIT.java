package com.prosilion.superconductor;

import com.fasterxml.jackson.databind.ObjectMapper;
import kotlin.jvm.Synchronized;
import lombok.Getter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.PropertySource;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Awaitility.given;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ContextConfiguration
@TestPropertySource("/application-test.properties")
class MultipleSubscriberTextEventMessageIT {
  private static final String TARGET_TEXT_MESSAGE_EVENT_CONTENT = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";

  @SuppressWarnings("preview")
  public final static String textMessageEventJson =
      StringTemplate.STR."""
          [
            "EVENT",
              {
                "content":"1111111111",
                "id":"\{TARGET_TEXT_MESSAGE_EVENT_CONTENT}",
                "kind":1,
                "created_at":1717357053050,
                "pubkey":"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984",
                "tags": [
                  [
                    "p",
                    "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984"
                  ],
                  [
                    "e",
                    "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"
                  ],
                  [
                    "g",
                    "textnote geo-tag-1"
                  ]
                ],
              "sig":"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546"
            }
          ]
      """;

  private static final String SCHEME_WS = "ws";
  private static final String HOST = "localhost";
  private static final String PORT = "5555";
  private static final String WEBSOCKET_URL = SCHEME_WS + "://" + HOST + ":" + PORT;

  private final ObjectMapper mapper = new ObjectMapper();
  private WebSocketStompClient eventStompClient;

  private final Integer targetCount;
  private final Integer pctThreshold;
//  private int resultCount = 0;
  private final LongAdder resultCount = new LongAdder();

  MultipleSubscriberTextEventMessageIT(
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      @Value("${superconductor.test.req.success_threshold_pct}") Integer pctThreshold) {
    this.targetCount = reqInstances;
    this.pctThreshold = pctThreshold;
  }

  @BeforeAll
  public void setup() {
    eventStompClient = new WebSocketStompClient(new StandardWebSocketClient());
    eventStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    CompletableFuture<WebSocketSession> eventExecute = eventStompClient.getWebSocketClient().execute(new EventMessageSocketHandler(), WEBSOCKET_URL, "");
    await().until(eventExecute::isDone);

    IntStream.range(0, targetCount).parallel().forEach(increment -> {
      final WebSocketStompClient reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());
      CompletableFuture<WebSocketSession> reqExecute = reqStompClient.getWebSocketClient().execute(new ReqMessageSocketHandler(increment, generateRandomHexString()), WEBSOCKET_URL, "");
      await().until(reqExecute::isDone);
//      reqStompClient.start();
    });
  }

  @Test
  void testEventMessageThenReqMessage() {
//    assertDoesNotThrow(() -> eventStompClient.start());
//    assertDoesNotThrow(() -> eventStompClient.stop());

    System.out.println("-------------------");
    System.out.printf("[%s/%s] == [%d%% of minimal %d%%] completed before test-container thread ended%n",
        resultCount.toString(),
        targetCount,
        ((resultCount.intValue() / targetCount) * 100),
        pctThreshold);
    System.out.println("-------------------");
  }


  static class EventMessageSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      session.sendMessage(new TextMessage(textMessageEventJson));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      assertTrue(message.getPayload().toString().contains(TARGET_TEXT_MESSAGE_EVENT_CONTENT));
      session.close();
    }
  }

  @Getter
  class ReqMessageSocketHandler extends TextWebSocketHandler {
    private final Integer index;
    private boolean value = false;
    private final String reqJson;

    public ReqMessageSocketHandler(Integer index, String reqId) {
      this.index = index;
      reqJson = "[\"REQ\",\"" + reqId + "\",{\"ids\":[\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      System.out.printf("AAAAAAAAAAAAAAAAAAAAAAAA[%02d], id: [%s]\n", index, session.getId());
      session.sendMessage(new TextMessage(reqJson));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      System.out.printf("BBBBBBBBBBBBBBBBBBBBBBBB[%02d], id: [%s]\n", index, session.getId());
      assertTrue(ComparatorWithoutOrder.equalsJson(mapper.readTree(textMessageEventJson), mapper.readTree(message.getPayload().toString())));

      if (!(ComparatorWithoutOrder.equalsJson(mapper.readTree(textMessageEventJson), mapper.readTree(message.getPayload().toString())))) {
        System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX[%02d]\n", index);
        System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX[%02d]\n", index);
        System.out.println(textMessageEventJson);
        System.out.printf("-----------------------------------------------[%02d]\n", index);
        System.out.println(message.getPayload().toString());
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
        throw new RuntimeException();
      }

      value = true;
      resultCount.increment();
//      session.close();
    }
  }

  private static String generateRandomHexString() {
    Random random = new Random();
    StringBuilder hexString = new StringBuilder();
    for (int i = 0; i < 32; i++) {
      int randomNumber = random.nextInt(16); // Generate a random number between 0 and 15 (inclusive)
      char hexDigit = (randomNumber < 10) ? (char) ('0' + randomNumber) : (char) ('a' + (randomNumber - 10));
      hexString.append(hexDigit);
    }
    return hexString.toString();
  }
}
