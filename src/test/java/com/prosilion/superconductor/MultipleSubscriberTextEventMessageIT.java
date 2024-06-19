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
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.test.annotation.DirtiesContext;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
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
                "tags":
                  [],
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
  private final LongAdder resultCount = new LongAdder();

  //  @Autowired
  //  private ServletContext servletContext;

  MultipleSubscriberTextEventMessageIT(
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      @Value("${superconductor.test.req.success_threshold_pct}") Integer pctThreshold) {
    this.targetCount = reqInstances;
    this.pctThreshold = pctThreshold;
  }

  @BeforeAll
  public void setup() {
    IntStream.range(0, targetCount).parallel().forEach(increment -> {
      final WebSocketStompClient reqStompClient = new WebSocketStompClient(new StandardWebSocketClient());
      reqStompClient.setMessageConverter(new MappingJackson2MessageConverter());
      CompletableFuture<WebSocketSession> execute = reqStompClient.getWebSocketClient().execute(new ReqMessageSocketHandler(increment, generateRandomHexString()), WEBSOCKET_URL, "");
      await().until(execute::isDone);
      reqStompClient.start();
    });

    eventStompClient = new WebSocketStompClient(new StandardWebSocketClient());
    eventStompClient.setMessageConverter(new MappingJackson2MessageConverter());
    CompletableFuture<WebSocketSession> execute = eventStompClient.getWebSocketClient().execute(new EventMessageSocketHandler(), WEBSOCKET_URL, "");

    await().until(execute::isDone);
  }

  @Test
  void executors() {
    //    ServletContext context = servletContext.getContext("/");
    //    Set<SessionTrackingMode> defaultSessionTrackingModes = servletContext.getDefaultSessionTrackingModes();
    //    Set<SessionTrackingMode> effectiveSessionTrackingModes = servletContext.getEffectiveSessionTrackingModes();
    //    Map<String, ? extends ServletRegistration> servletRegistrations = servletContext.getServletRegistrations();
    //    Enumeration<String> attributeNames = servletContext.getAttributeNames();
  }

  @Test
  void testEventMessageThenReqMessage() {
    assertDoesNotThrow(() -> eventStompClient.start());
    assertDoesNotThrow(() -> eventStompClient.stop());

    long percentSuccessThreshold = Math.round(targetCount - (targetCount * (1 - pctThreshold * .01)));

    await().untilAdder(resultCount, greaterThanOrEqualTo(percentSuccessThreshold));

    given().ignoreException(InterruptedException.class)
        .await().until(() -> !eventStompClient.isRunning());

    System.out.println("-------------------");
    System.out.printf("[%s/%s] == [%d%% of minimal %d%%] completed before test-container thread ended%n",
        resultCount,
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

  @Synchronized
  private void incrementRange() {
    resultCount.increment();
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
      session.sendMessage(new TextMessage(reqJson));
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
      assertEquals(mapper.readTree(textMessageEventJson), mapper.readTree(message.getPayload().toString()));
      value = true;
      incrementRange();
      session.close();
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
