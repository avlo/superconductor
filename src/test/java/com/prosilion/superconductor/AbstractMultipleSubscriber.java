package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prosilion.superconductor.util.NostrRelayService;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
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

  AbstractMultipleSubscriber(
      NostrRelayService nostrRelayService,
      String hexCounterSeed,
      Integer reqInstances) {
    this.nostrRelayService = nostrRelayService;
    this.hexCounterSeed = hexCounterSeed;
    this.hexStartNumber = Integer.parseInt(hexCounterSeed, 16);
    this.targetCount = reqInstances;
  }

  @BeforeAll
  public void setup() throws IOException {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
            IntStream.range(0, targetCount).forEach(value ->
                assertAll(() -> createEvent(value)))
        , executorService);

    await()
        .timeout(1, TimeUnit.MINUTES)
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
  }

  abstract String getGlobalEventJson(@NonNull String startEventId);
  abstract String getExpectedJsonInAnyOrder(@NonNull String startEventId);
  abstract String createReqJson(@NonNull String uuid);

  @Test
  @Order(0)
  void testReqMessage() {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
            IntStream.range(0, targetCount).forEach(value ->
                assertAll(() -> sendRequest(value)))
        , executorService);

    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(voidCompletableFuture::isDone);

    assertFalse(voidCompletableFuture.isCompletedExceptionally());

    log.debug("testReqMessageWithExecutor requests completed after elapsed time [{}]", System.currentTimeMillis() - start);
  }

  private void sendRequest(int increment) throws IOException, ExecutionException, InterruptedException {
    String uuidKey = getNextHex(increment);
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(uuidKey),
        uuidKey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
    String responseJson = returnedJsonMap.get(Command.EVENT).get();
    String expectedJsonInAnyOrder = getExpectedJsonInAnyOrder(uuidKey);
    log.debug("expectedJson:\n  {}", expectedJsonInAnyOrder);
    log.debug("------------");
    log.debug("responseJson:\n  {}", responseJson);
    assertTrue(responseJson.contains(uuidKey));
    assertTrue(compareWithoutOrder(responseJson, expectedJsonInAnyOrder));
  }


  protected String getNextHex(int i) {
    String incrementedHexNumber = Integer.toHexString(hexStartNumber + i);
    return hexCounterSeed
        .substring(0, hexCounterSeed.length() - incrementedHexNumber.length())
        .concat(incrementedHexNumber);
  }

  protected boolean compareWithoutOrder(String payloadString, String expectedJson) throws JsonProcessingException {
    JsonNode jsonNode = mapper.readTree(payloadString);
    return ComparatorWithoutOrder.equalsJson(mapper.readTree(expectedJson), jsonNode);
  }
}
