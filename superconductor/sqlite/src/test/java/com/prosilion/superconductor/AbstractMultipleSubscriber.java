package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import com.prosilion.superconductor.util.OrderAgnosticJsonComparator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class) // exists because MultipleSubscriberEventIdAndAuthorIT has additional tests
abstract class AbstractMultipleSubscriber {
  private final static ObjectMapper MAPPER_AFTERBURNER = JsonMapper.builder().addModule(new AfterburnerModule()).build();
  @Getter
  private final Integer targetCount;
  @Getter
  private final NostrRelayService nostrRelayService;
  @Getter
  private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  private final int hexStartNumber;
  @Getter
  private final List<String> targetEventIds = new ArrayList<>();

  AbstractMultipleSubscriber(
      NostrRelayService nostrRelayService,
      String hexCounterSeed,
      Integer hexNumberOfBytes,
      Integer reqInstances) {
    this.nostrRelayService = nostrRelayService;
    this.hexStartNumber = Integer.parseInt(hexCounterSeed.repeat(2 * hexNumberOfBytes), 16);
    this.targetCount = reqInstances;
  }

  @BeforeAll
  public void beforeAll() {
    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
            IntStream.range(0, targetCount).forEach(value ->
                assertAll(() -> createEvent(value)))
        , executorService);

    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(voidCompletableFuture::isDone);

    assertFalse(voidCompletableFuture.isCompletedExceptionally());
  }

  private void createEvent(int increment) throws IOException {
    String nextHex = getNextHex(increment);
    log.debug("next hex: {}", nextHex);
    String globalEventJson = getGlobalEventJson(nextHex);
    log.debug("setup() send event:\n  {}", globalEventJson);
//    TODO: update cast
    nostrRelayService.send((EventMessage) BaseMessageDecoder.decode(globalEventJson));
    targetEventIds.add(nextHex); // targetEventId String values utilized by inherited classes
  }

  abstract String getGlobalEventJson(String startEventId);

  abstract String getExpectedJsonInAnyOrder(String startEventId);

  abstract String createReqJson(String uuid);

  @Test
  @Order(0)
  void testReqMessage() {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
            targetEventIds.forEach(value ->
                assertAll(() -> sendRequest(value)))
        , executorService);

    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(voidCompletableFuture::isDone);

    assertFalse(voidCompletableFuture.isCompletedExceptionally());

    log.debug("testReqMessageWithExecutor requests completed after elapsed time [{}]", System.currentTimeMillis() - start);
  }

  private void sendRequest(String uuidKey) throws JsonProcessingException, NostrException {
    List<BaseMessage> send = nostrRelayService.send(
        (ReqMessage) BaseMessageDecoder.decode(createReqJson(uuidKey)));
    String expectedJsonInAnyOrder = getExpectedJsonInAnyOrder(uuidKey);
    log.debug("expectedJson:\n  {}", expectedJsonInAnyOrder);
  }

  protected synchronized String getNextHex(int i) {
    String incrementedHexNumber = Integer.toHexString(hexStartNumber + i);
    log.debug("incrementedHexNumber:\n  [{}]", incrementedHexNumber);
    return Factory.generateRandomHex64String()
        .substring(0, 64 - incrementedHexNumber.length())
        .concat(incrementedHexNumber);
  }

  protected static boolean compareWithoutOrder(String payloadString, String expectedJson) throws JsonProcessingException {
    JsonNode jsonNode = MAPPER_AFTERBURNER.readTree(payloadString);
    return OrderAgnosticJsonComparator.equalsJson(MAPPER_AFTERBURNER.readTree(expectedJson), jsonNode);
  }
}
