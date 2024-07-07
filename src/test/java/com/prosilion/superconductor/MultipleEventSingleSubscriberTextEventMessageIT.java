package com.prosilion.superconductor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Synchronized;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.expression.EvaluationException;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.stereotype.Component;
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

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ContextConfiguration
@TestPropertySource("/application-test.properties")
class MultipleEventSingleSubscriberTextEventMessageIT {

  @Autowired
  ApplicationContext applicationContext;
  public final String textMessageEventJson;
  public final String textMessageEventJsonReordered;

  private static final String SCHEME_WS = "ws";
  private static final String HOST = "localhost";
  private final String websocketUrl;

  private final String hexCounterSeed;
  private final int hexStartNumber;
  private final Integer targetCount;
  private final Integer pctThreshold;
  int resultCount;

  private final ExecutorService executorService;

  List<Callable<CompletableFuture<WebSocketSession>>> reqClients;
  List<Callable<CompletableFuture<WebSocketSession>>> eventClients;

  MultipleEventSingleSubscriberTextEventMessageIT(
      @Value("${server.port}") String port,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      @Value("${superconductor.test.req.success_threshold_pct}") Integer pctThreshold) throws IOException {
    this.websocketUrl = SCHEME_WS + "://" + HOST + ":" + port;
    this.hexCounterSeed = hexCounterSeed;
    this.hexStartNumber = Integer.parseInt(hexCounterSeed);
    this.targetCount = reqInstances;
    this.pctThreshold = pctThreshold;
    this.reqClients = new ArrayList<>(targetCount);
    this.eventClients = new ArrayList<>(targetCount);
    this.executorService = MoreExecutors.newDirectExecutorService();

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/multiple_text_event_json_input.txt"))) {
      this.textMessageEventJson = lines.collect(Collectors.joining("\n"));
    }

    try (Stream<String> lines = Files.lines(Paths.get("src/test/resources/multiple_text_event_json_reordered.txt"))) {
      this.textMessageEventJsonReordered = lines.collect(Collectors.joining("\n"));
    }
  }

  @BeforeAll
  public void setup() {
    IntStream.range(0, targetCount).boxed().forEach(eventIdIncrement -> {
      final WebSocketStompClient eventStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      eventStompClient.setMessageConverter(new MappingJackson2MessageConverter());

      final EventMessageSocketHandler eventHandlerBean = applicationContext.getBean(EventMessageSocketHandler.class);
      eventHandlerBean.setEventJson(textMessageEventJson, getNextHex(eventIdIncrement));
      Callable<CompletableFuture<WebSocketSession>> callableEventTask = () ->
          eventStompClient.getWebSocketClient().execute(eventHandlerBean, websocketUrl, "");
      eventClients.add(callableEventTask);

      final WebSocketStompClient reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());

      final ReqMessageSocketHandler reqHandlerBean = applicationContext.getBean(ReqMessageSocketHandler.class);
      reqHandlerBean.setEventJson(textMessageEventJsonReordered, getNextHex(eventIdIncrement));
      Callable<CompletableFuture<WebSocketSession>> callableReqTask = () ->
          reqStompClient.getWebSocketClient().execute(reqHandlerBean, websocketUrl, "");
      reqClients.add(callableReqTask);
    });

    assertDoesNotThrow(() -> executorService.invokeAll(eventClients).stream().forEachOrdered(future ->
        await().until(() -> future.get().isDone())));

    assertDoesNotThrow(() -> executorService.invokeAll(reqClients).stream().forEachOrdered(future ->
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
    System.out.println("((resultCount / targetCount) * 100): " + (((resultCount - 1) / targetCount) * 100));
    System.out.printf("[%s/%s] == [%d%% of minimal %d%%] completed before test-container thread ended%n",
        (resultCount - 1),
        targetCount,
        (((resultCount - 1) / targetCount) * 100),
        pctThreshold);
    System.out.println("-------------------");
  }

  @Synchronized
  void increment() {
    resultCount++;
  }

  private String getNextHex(int i) {
    final String incrementedHexNumber = Integer.toHexString(hexStartNumber + i);
    final String concat = hexCounterSeed
        .substring(0, hexCounterSeed.length() - incrementedHexNumber.length())
        .concat(incrementedHexNumber);
    return concat;
  }
}

@Component
@Scope("prototype")
class EventMessageSocketHandler extends TextWebSocketHandler {
  private String eventJson;

  public void setEventJson(String textMessageEventJson, String eventId) {
    this.eventJson = textMessageEventJson.replace("EVENT_ID", eventId);
  }

  @Override
  public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
//      EventMessageSocketHandler eventMessageSocketHandler = this;
//      int x = eventMessageSocketHandler.hashCode();
//      System.out.println("EventMessageSocketHandler connection: " + x + " = " + eventJson);
    session.sendMessage(new TextMessage(eventJson));
  }

  @Override
  public void handleMessage(final WebSocketSession session, final WebSocketMessage<?> message) throws Exception {
//      EventMessageSocketHandler eventMessageSocketHandler = this;
//      int x = eventMessageSocketHandler.hashCode();
//      System.out.println("EventMessageSocketHandler message: " + x + " = " + eventJson);
//      assertTrue(message.getPayload().toString().contains(eventJson));
    session.close();
    await().until(() -> !session.isOpen());
  }
}

@Component
@Scope("prototype")
class ReqMessageSocketHandler extends TextWebSocketHandler {
  private final ObjectMapper mapper = new ObjectMapper();
  private String reqJson;
  private String eventJson;

  public void setEventJson(String textMessageEventJsonReordered, String eventId) {
    this.eventJson = textMessageEventJsonReordered.replace("EVENT_ID", eventId);
    this.reqJson = "[\"REQ\",\"" + eventId + "\",{\"ids\":[\"" + eventId + "\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";
  }

  @Override
  public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
//      ReqMessageSocketHandler handler = this;
//      int x = handler.hashCode();
//      System.out.println("ReqMessageSocketHandler connection: " + x + " = " + reqJson);
    session.sendMessage(new TextMessage(reqJson));
  }

  @Override
  public void handleMessage(@NotNull final WebSocketSession session, final WebSocketMessage<?> message) throws EvaluationException, IOException {
//      ReqMessageSocketHandler handler = this;
//      int x = handler.hashCode();
//      System.out.println("ReqMessageSocketHandler message: " + x + " = " + eventJson);
    boolean condition = ComparatorWithoutOrder.equalsJson(mapper.readTree(eventJson), mapper.readTree(message.getPayload().toString()));

    if (!condition) {
      session.close();
      throw new EvaluationException(String.format("Json doesnt' match.  Expected value:%n%s%n but received:%n%s%n", eventJson, mapper.readTree(message.getPayload().toString()).toPrettyString()));
    }
    session.close();
    await().until(() -> !session.isOpen());
  }
}
