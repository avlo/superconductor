package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prosilion.superconductor.util.Factory;
import com.prosilion.superconductor.util.NostrRelayService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class) // exists because MultipleSubscriberEventIdAndAuthorIT has additional tests
abstract class AbstractMultipleSubscriber {
  private final ObjectMapper mapper = new ObjectMapper();
  @Getter
  private final String hexCounterSeed;
  @Getter
  private final Integer targetCount;
  @Getter
  private final NostrRelayService nostrRelayService;
  @Getter
  private final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();

  private final int hexStartNumber;
  private final List<String> targetEventIds = new ArrayList<>();

  AbstractMultipleSubscriber(
      NostrRelayService nostrRelayService,
      String hexCounterSeed,
      Integer hexNumberOfBytes,
      Integer reqInstances) {
    this.nostrRelayService = nostrRelayService;
    this.hexCounterSeed = hexCounterSeed.repeat(2 * hexNumberOfBytes);
    this.hexStartNumber = Integer.parseInt(hexCounterSeed, 16);
    this.targetCount = 1;
  }

  @BeforeEach
  public void setup() throws IOException {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
            IntStream.range(0, targetCount).forEach(value ->
                assertAll(() -> createEvent(value)))
        , executorService);

    await()
        .timeout(10, TimeUnit.MINUTES)
        .until(voidCompletableFuture::isDone);

    assertFalse(voidCompletableFuture.isCompletedExceptionally());

    log.info("events setup elapsed time [{}]", System.currentTimeMillis() - start);
  }

  private void createEvent(int increment) throws IOException {
    String nextHex = getNextHex(increment);
    log.debug("next hex: {}", nextHex);
    String globalEventJson = getGlobalEventJson(nextHex);
    log.debug("setup() send event:\n  {}", globalEventJson);
    nostrRelayService.createEvent(globalEventJson);
    targetEventIds.add(nextHex); // used as subscriberIds for (currently one) test MultipleSubscriberTextEventMessageIT
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

  private void sendRequest(String uuidKey) throws IOException, ExecutionException, InterruptedException {
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(uuidKey),
        uuidKey
    );
    log.debug("okMessage:\n  {}", returnedJsonMap);
    String responseJson = Optional.of(returnedJsonMap.get(Command.EVENT)).get().orElseThrow();
    String expectedJsonInAnyOrder = getExpectedJsonInAnyOrder(uuidKey);
    log.debug("expectedJson:\n  {}", expectedJsonInAnyOrder);
    log.debug("------------");
    log.debug("responseJson:\n  {}", responseJson);
    assertTrue(responseJson.contains(uuidKey));
    assertTrue(compareWithoutOrder(responseJson, expectedJsonInAnyOrder));
  }


  private String getNextHex(int i) {
    String incrementedHexNumber = Integer.toHexString(hexStartNumber + i);
    log.debug("incrementedHexNumber:\n  [{}]", incrementedHexNumber);
    return Factory.generateRandomHex64String()
        .substring(0, 64 - incrementedHexNumber.length())
        .concat(incrementedHexNumber);
  }

  protected boolean compareWithoutOrder(String payloadString, String expectedJson) throws JsonProcessingException {
    JsonNode jsonNode = mapper.readTree(payloadString);
    return ComparatorWithoutOrder.equalsJson(mapper.readTree(expectedJson), jsonNode);
  }
}
