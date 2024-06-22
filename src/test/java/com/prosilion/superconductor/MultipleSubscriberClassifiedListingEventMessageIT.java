package com.prosilion.superconductor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Synchronized;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.expression.EvaluationException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ContextConfiguration
@TestPropertySource("/application-test.properties")
class MultipleSubscriberClassifiedListingEventMessageIT {
  private static final String TARGET_CLASSIFIED_LISTING_EVENT_CONTENT = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";

  @SuppressWarnings("preview")
  public final static String classifiedListingEventJsonSent =
      StringTemplate.STR."""
          [
            "EVENT",
            {
              "id":"\{TARGET_CLASSIFIED_LISTING_EVENT_CONTENT}",
              "kind": 30402,
              "content": "classified content",
              "tags": [
                [
                  "subject",
                  "classified subject"
                ],
                [
                  "title",
                  "classified title"
                ],
                [
                  "published_at",
                  "1718843251760"
                ],
                [
                  "summary",
                  "classified summary"
                ],
                [
                  "location",
                  "classified peroulades"
                ],
                [
                  "price",
                  "271.00",
                  "BTC",
                  "1"
                ],
                [
                  "e",
                  "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"
                ],
                [
                  "p",
                  "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984"
                ],
                [
                  "t",
                  "classified hash-tag-1111"
                ],
                [
                  "g",
                  "classified geo-tag-1"
                ]
              ],
              "pubkey": "cccd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984",
              "created_at": 1718843251760,
              "sig": "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546"
            }
          ]
      """;

  @SuppressWarnings("preview")
  public final static String classifiedListingEventJsonExpectedWithReordering =
      StringTemplate.STR."""
          [
            "EVENT",
            {
              "id":"\{TARGET_CLASSIFIED_LISTING_EVENT_CONTENT}",
              "kind": 30402,
              "content": "classified content",
              "tags": [
                [
                  "price",
                  "271.00",
                  "BTC",
                  "1"
                ],
                [
                  "subject",
                  "classified subject"
                ],
                [
                  "published_at",
                  "1718843251760"
                ],
                [
                  "title",
                  "classified title"
                ],
                [
                  "summary",
                  "classified summary"
                ],
                [
                  "location",
                  "classified peroulades"
                ],
                [
                  "e",
                  "494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346"
                ],
                [
                  "p",
                  "2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984"
                ],
                [
                  "t",
                  "classified hash-tag-1111"
                ],
                [
                  "g",
                  "classified geo-tag-1"
                ]
              ],
              "pubkey": "cccd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984",
              "created_at": 1718843251760,
              "sig": "86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546"
            }
          ]
      """;

  private static final String SCHEME_WS = "ws";
  private static final String HOST = "localhost";
  private final String websocketUrl;

  private final String hexCounterSeed;
  private final int hexStartNumber;
  private final Integer targetCount;
  private final Integer pctThreshold;
  int resultCount;

  private final ObjectMapper mapper = new ObjectMapper();
  private final ExecutorService executorService;

  List<Callable<CompletableFuture<WebSocketSession>>> reqClients;

  MultipleSubscriberClassifiedListingEventMessageIT(
      @Value("${server.port}") String port,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      @Value("${superconductor.test.req.success_threshold_pct}") Integer pctThreshold) {
    this.websocketUrl = SCHEME_WS + "://" + HOST + ":" + port;
    this.hexCounterSeed = hexCounterSeed;
    this.hexStartNumber = Integer.parseInt(hexCounterSeed, 16);
    this.targetCount = reqInstances;
    this.pctThreshold = pctThreshold;
    this.executorService = Executors.newSingleThreadExecutor();
  }

  @BeforeAll
  public void setup() throws InterruptedException {
    WebSocketStompClient eventStompClient = new WebSocketStompClient(new StandardWebSocketClient());
    eventStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    CompletableFuture<WebSocketSession> eventExecute = eventStompClient.getWebSocketClient().execute(new EventMessageSocketHandler(), websocketUrl, "");
    await().until(eventExecute::isDone);

    reqClients = new ArrayList<>(targetCount);
    IntStream.range(0, targetCount).sequential().forEach(increment -> {
      final WebSocketStompClient reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());

      Callable<CompletableFuture<WebSocketSession>> callableTask = () -> {
        return reqStompClient.getWebSocketClient().execute(new ReqMessageSocketHandler(increment, getNextHex(increment)), websocketUrl, "");
      };
      reqClients.add(callableTask);
    });
//    System.out.println("reqClients count (1): " + reqClients.size());
    assertDoesNotThrow(() -> executorService.invokeAll(reqClients).stream().parallel().forEach(future ->
        await().until(() -> future.get().isDone())));
  }

  @Test
  void testEventMessageThenReqMessage() {
//    System.out.println("reqClients count (2): " + reqClients.size());

//    expected below to catch internal thread exception, but did not
//        await().until(() -> {
//          try {
//            future.get().isDone();
//          } catch (EvaluationException e) {
//            System.out.println("XXXXXXXXXXXXXXXXXXXX");
//            System.out.println("XXXXXXXXXXXXXXXXXXXX");
//          }
//          return true;
//        }));

    executorService.shutdown();
    await().until(() -> executorService.awaitTermination(5000, TimeUnit.SECONDS));
    await().until(executorService::isTerminated);

    System.out.println("-------------------");
    System.out.printf("[%s/%s] == [%d%% of minimal %d%%] completed before test-container thread ended%n",
        resultCount,
        targetCount,
        ((resultCount / targetCount) * 100),
        pctThreshold);
    System.out.println("-------------------");
  }


  static class EventMessageSocketHandler extends TextWebSocketHandler {
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      session.sendMessage(new TextMessage(classifiedListingEventJsonSent));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      assertTrue(message.getPayload().toString().contains(TARGET_CLASSIFIED_LISTING_EVENT_CONTENT));
      session.close();
    }
  }

  @Getter
  class ReqMessageSocketHandler extends TextWebSocketHandler {
    private final Integer index;
    private final String reqJson;

    public ReqMessageSocketHandler(Integer index, String reqId) {
      this.index = index;
      reqJson = "[\"REQ\",\"" + reqId + "\",{\"ids\":[\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
//      System.out.printf("AAAAAAAAAAAAAAAAAAAAAAAA[%02d], id: [%s]\n", index, session.getId());
      session.sendMessage(new TextMessage(reqJson));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws EvaluationException, IOException {
      JsonNode payloadString = mapper.readTree(message.getPayload().toString());
      boolean condition = ComparatorWithoutOrder.equalsJson(mapper.readTree(classifiedListingEventJsonExpectedWithReordering), payloadString);

      System.out.printf("BBBBBBBBBBBBBBBBBBBBBBBB[%02d], match: [%s]\n", index, condition);
      System.out.println(payloadString.toPrettyString());
      System.out.println("------------------------");
//    below sout seems to serve extending thread execution time, preventing its premature shutdown
      session.close();
      increment();
      if (!condition) {
//        System.out.println("CCCCCCCCCCCCCCCCCCCCCCCC");
        throw new EvaluationException(String.format("Json doesnt' match.  Expected value:%n%s%n but received:%n%s%n", classifiedListingEventJsonExpectedWithReordering, payloadString.toPrettyString()));
      }
    }
  }

  @Synchronized
  void increment() {
    resultCount++;
  }

  private String getNextHex(int i) {
    String incrementedHexNumber = Integer.toHexString(hexStartNumber + i);
    return hexCounterSeed
        .substring(0, hexCounterSeed.length() - incrementedHexNumber.length())
        .concat(incrementedHexNumber);
  }
}
