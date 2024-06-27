package com.prosilion.superconductor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.Getter;
import lombok.Synchronized;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
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

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ContextConfiguration
@TestPropertySource("/application-test.properties")
class MultipleSubscriberTextEventMessageITRxR {
  private static final String SCHEME_WS = "ws";
  private static final String HOST = "localhost";
  private final String websocketUrl;

  private final String hexCounterSeed;
  private final int hexStartNumber;
  private final Integer targetCount;
  private final Integer pctThreshold;
  int resultCount;

  private ObjectMapper mapper;
  private ExecutorService executorService;

  MultipleSubscriberTextEventMessageITRxR(
      @Value("${server.port}") String port,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      @Value("${superconductor.test.req.success_threshold_pct}") Integer pctThreshold) {
    this.websocketUrl = SCHEME_WS + "://" + HOST + ":" + port;
    this.hexCounterSeed = hexCounterSeed;
    this.hexStartNumber = Integer.parseInt(hexCounterSeed, 16);
    this.targetCount = reqInstances;
    this.pctThreshold = pctThreshold;

    this.executorService = MoreExecutors.newDirectExecutorService();
    this.mapper = new ObjectMapper();
    this.resultCount = 0;
  }

  @Test
  void testTextEventMessage() throws IOException, InterruptedException {
    testEventMessageThenReqMessage(
        Files.lines(Paths.get("src/test/resources/text_event_json_input.txt")).collect(Collectors.joining("\n")),
        Files.lines(Paths.get("src/test/resources/text_event_json_reordered.txt")).collect(Collectors.joining("\n"))
    );
  }

  private void testEventMessageThenReqMessage(String inJson, String expectedJson) throws InterruptedException {
    WebSocketStompClient eventStompClient = new WebSocketStompClient(new StandardWebSocketClient());
    eventStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    Callable<CompletableFuture<WebSocketSession>> eventExecute = () -> eventStompClient.getWebSocketClient().execute(new EventMessageSocketHandler(inJson), websocketUrl, "");
    executorService.submit(eventExecute);

    List<Callable<CompletableFuture<WebSocketSession>>> reqClients = new ArrayList<>(targetCount);
    IntStream.range(0, targetCount).parallel().forEach(increment -> {
      WebSocketStompClient reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());

      Callable<CompletableFuture<WebSocketSession>> callableTask = () ->
          reqStompClient.getWebSocketClient().execute(
              new ReqMessageSocketHandler(
                  increment,
                  getNextHex(increment),
                  expectedJson),
              websocketUrl,
              "");
      reqClients.add(callableTask);
    });

    executorService.invokeAll(reqClients);

//    assertDoesNotThrow(() -> executorService.invokeAll(reqClients).stream().parallel().forEach(future ->
//        await().until(() -> future.get().isDone())));

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

  class EventMessageSocketHandler extends TextWebSocketHandler {
    private final String inJson;

    public EventMessageSocketHandler(String inJson) {
      this.inJson = inJson;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      session.sendMessage(new TextMessage(inJson));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      assertTrue(message.getPayload().toString().contains("5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc"));
      session.close();
    }
  }

  @Getter
  class ReqMessageSocketHandler extends TextWebSocketHandler {
    private final Integer index;
    private final String reqJson;
    private final String expectedJson;

    public ReqMessageSocketHandler(Integer index, String reqId, String expectedJson) {
      this.index = index;
      this.expectedJson = expectedJson;
      reqJson = "[\"REQ\",\"" + reqId + "\",{\"ids\":[\"5f66a36101d3d152c6270e18f5622d1f8bce4ac5da9ab62d7c3cc0006e5914cc\"],\"authors\":[\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"]}]";
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
      session.sendMessage(new TextMessage(reqJson));
    }

    @Override
    public void handleMessage(@NotNull WebSocketSession session, WebSocketMessage<?> message) throws EvaluationException, IOException {
      boolean condition = ComparatorWithoutOrder.equalsJson(mapper.readTree(expectedJson), mapper.readTree(message.getPayload().toString()));

      if (!condition) {
        session.close();
        throw new EvaluationException(String.format("Json doesnt' match.  Expected value:%n%s%n but received:%n%s%n", expectedJson, mapper.readTree(message.getPayload().toString()).toPrettyString()));
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
