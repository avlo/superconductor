package com.prosilion.superconductor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Synchronized;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
//@DirtiesContext
@ExtendWith(SpringExtension.class)
@TestPropertySource("/application-test.properties")
class MultipleSubscriberTextEventMessageIT {
  private static final String TARGET_TEXT_MESSAGE_EVENT_CONTENT = "1111111111111111111111111111111111111111111111111111111111111111";

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

  public final String hexCounterSeed;
  private final int hexStartNumber;
  private final Integer targetCount;
  private final Integer pctThreshold;

  //  private final LongAdder resultCount = new LongAdder();
  int resultCount;
  int hexCount = 0;
  private final ExecutorService executorService;


  MultipleSubscriberTextEventMessageIT(
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      @Value("${superconductor.test.req.success_threshold_pct}") Integer pctThreshold) {
    this.hexCounterSeed = hexCounterSeed;
    this.hexStartNumber = Integer.parseInt(hexCounterSeed, 16);
    this.targetCount = reqInstances;
    this.pctThreshold = pctThreshold;
    executorService = Executors.newFixedThreadPool(reqInstances);
  }

  @BeforeAll
  public void setup() throws InterruptedException {
    WebSocketStompClient eventStompClient = new WebSocketStompClient(new StandardWebSocketClient());
    eventStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    CompletableFuture<WebSocketSession> eventExecute = eventStompClient.getWebSocketClient().execute(new EventMessageSocketHandler(), WEBSOCKET_URL, "");
    await().until(eventExecute::isDone);

    List<Callable<CompletableFuture<WebSocketSession>>> reqClients = new ArrayList<>(targetCount);
    IntStream.range(1, targetCount+1).parallel().forEach(increment -> {
      final WebSocketStompClient reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());

      Callable<CompletableFuture<WebSocketSession>> callableTask = () -> {
        CompletableFuture<WebSocketSession> reqExecute = reqStompClient.getWebSocketClient().execute(new ReqMessageSocketHandler(increment, getNextHex(increment)), WEBSOCKET_URL, "");
//        await().until(reqExecute::isDone);
//        System.out.println("++++++++++++++++++++");
//        System.out.println(increment);
//        System.out.println("++++++++++++++++++++");
        return reqExecute;
      };
      reqClients.add(callableTask);
    });
    executorService.invokeAll(reqClients);
//      reqStompClient.start();
  }

//  @Test
  void testEventMessageThenReqMessage() {
//    assertDoesNotThrow(() -> eventStompClient.start());
//    assertDoesNotThrow(() -> eventStompClient.stop());
    executorService.shutdown();
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
      increment();
//      session.close();
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
    }
  }

  @Synchronized
  void increment() {
    resultCount++;
  }

  @Test
  void another() {
    IntStream.range(1, targetCount+1).forEach(this::getNextHex);
  }

  String getNextHex(int i) {
    String incrementedHexNumber = Integer.toHexString(hexStartNumber + i);
    String incrementedHexString = hexCounterSeed
        .substring(0, hexCounterSeed.length() - incrementedHexNumber.length())
        .concat(incrementedHexNumber);
    System.out.println(incrementedHexString);
    return incrementedHexString;
  }
}
