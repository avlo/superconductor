package com.prosilion.superconductor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import lombok.Synchronized;
import org.jetbrains.annotations.NotNull;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ContextConfiguration
@TestPropertySource("/application-test.properties")
class MultipleSubscriberClassifiedListingEventMessageIT {
  private static final String TARGET_TEXT_MESSAGE_EVENT_CONTENT = "5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc";

  public final String textMessageEventJson;
  public final String textMessageEventJsonReordered;

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
      @Value("${superconductor.relay.url}") String relayUrl,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      @Value("${superconductor.test.req.success_threshold_pct}") Integer pctThreshold) throws IOException {
    this.websocketUrl = relayUrl;
    this.hexCounterSeed = hexCounterSeed;
    this.hexStartNumber = Integer.parseInt(hexCounterSeed, 16);
    this.targetCount = reqInstances;
    this.pctThreshold = pctThreshold;
    this.executorService = MoreExecutors.newDirectExecutorService();

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/classified_listing_event_json_input.txt"))) {
      this.textMessageEventJson = lines.collect(Collectors.joining("\n"));
    }

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/classified_listing_event_json_reordered.txt"))) {
      this.textMessageEventJsonReordered = lines.collect(Collectors.joining("\n"));
    }
  }

  @BeforeAll
  public void setup() {
    WebSocketStompClient eventStompClient = new WebSocketStompClient(new StandardWebSocketClient());
    eventStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    CompletableFuture<WebSocketSession> eventExecute = eventStompClient.getWebSocketClient().execute(new EventMessageSocketHandler(), websocketUrl, "");
    await().until(eventExecute::isDone);

    reqClients = new ArrayList<>(targetCount);
    IntStream.range(0, targetCount).parallel().forEach(increment -> {
      final WebSocketStompClient reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());

      Callable<CompletableFuture<WebSocketSession>> callableTask = () ->
          reqStompClient.getWebSocketClient().execute(new ReqMessageSocketHandler(increment, getNextHex(increment)), websocketUrl, "");
      reqClients.add(callableTask);
    });
    assertDoesNotThrow(() -> executorService.invokeAll(reqClients).stream().parallel().forEach(future ->
        await().until(() -> future.get().isDone())));
  }

  @Test
  void testEventMessageThenReqMessage() {
    executorService.shutdown();
    await().until(() -> executorService.awaitTermination(5000, TimeUnit.SECONDS));
    await().until(executorService::isTerminated);

    System.out.println("-------------------");
    System.out.println("resultCount: " + resultCount);
    System.out.println("targetCount: " + targetCount);
    System.out.println("((resultCount / targetCount) * 100): " + ((resultCount / targetCount) * 100));
    System.out.printf("[%s/%s] == [%d%% of minimal %d%%] completed before test-container thread ended%n",
        resultCount,
        targetCount,
        ((resultCount / targetCount) * 100),
        pctThreshold);
    System.out.println("-------------------");
  }


  class EventMessageSocketHandler extends TextWebSocketHandler {
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
      reqJson = "[\"REQ\",\"0000000000000000000000000000000000000000000000000\",{\"ids\":[\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      session.sendMessage(new TextMessage(reqJson));
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, WebSocketMessage<?> message) throws EvaluationException, IOException {
      boolean condition = ComparatorWithoutOrder.equalsJson(mapper.readTree(textMessageEventJsonReordered), mapper.readTree(message.getPayload().toString()));

      if (!condition) {
        session.close();
        throw new EvaluationException(String.format("Json doesnt' match.  Expected value:%n%s%n but received:%n%s%n", textMessageEventJsonReordered, mapper.readTree(message.getPayload().toString()).toPrettyString()));
      }
      increment();
      session.close();
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
