package com.prosilion.superconductor;

import com.google.common.util.concurrent.MoreExecutors;
import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class MultipleSubscriberTextEventMessageIT {
  private final String hexCounterSeed;
  private final int hexStartNumber;
  private final Integer targetCount;

  private final NostrRelayService nostrRelayService;
  private final String uuidPrefix;

  private final ExecutorService executorService;

  @Autowired
  MultipleSubscriberTextEventMessageIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.instances}") Integer reqInstances,
      @Value("${superconductor.test.req.success_threshold_pct}") Integer pctThreshold) {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;
    this.hexCounterSeed = hexCounterSeed;
    this.hexStartNumber = Integer.parseInt(hexCounterSeed, 16);
    this.targetCount = reqInstances;
    this.executorService = MoreExecutors.newDirectExecutorService();
  }

  @BeforeAll
  public void setup() throws IOException {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
        IntStream.range(0, targetCount).forEach(this::createEvent), executorService);
    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(voidCompletableFuture::isDone);

//    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
//        IntStream.range(0, targetCount).forEach(this::createEvent));
//    await()
//        .timeout(1, TimeUnit.MINUTES)
//        .until(voidCompletableFuture::isDone);

    log.info("events setup elapsed time [{}]", System.currentTimeMillis() - start);
  }

  @SneakyThrows
  private void createEvent(int increment) {
    String nextHex = getNextHex(increment);
    log.debug("next hex: {}", nextHex);
    String globalEventJson = getGlobalEventJson(nextHex);
    log.debug("setup() send event:\n  {}", globalEventJson);
    nostrRelayService.createEvent(globalEventJson);
  }

  private String getGlobalEventJson(String startEventId) {
    return "[\"EVENT\",{\"id\":\"" + startEventId + "\",\"kind\":1,\"content\":\"1111111111\",\"pubkey\":\"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
  }

  @Test
  @Order(0)
  void testReqMessageWithExecutor() {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> requestFutures = CompletableFuture.runAsync(() ->
        IntStream.range(0, targetCount).forEach(this::sendRequest), executorService);
    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(requestFutures::isDone);

    log.debug("testReqMessageWithExecutor requests completed after elapsed time [{}]", System.currentTimeMillis() - start);
  }

//  @Test
//  @Order(1)
//  void testReqMessageWithoutExecutor() {
//    long start = System.currentTimeMillis();
//
//    CompletableFuture<Void> requestFutures = CompletableFuture.runAsync(() ->
//        IntStream.range(0, targetCount).forEach(this::sendRequest));
//    await()
//        .timeout(1, TimeUnit.MINUTES)
//        .until(requestFutures::isDone);
//
//    log.debug("testReqMessageWithoutExecutor requests completed after elapsed time [{}]", System.currentTimeMillis() - start);
//  }

  @SneakyThrows
  private void sendRequest(int increment) {
    String uuidKey = getNextHex(increment);
    Map<Command, Optional<String>> returnedJsonMap = nostrRelayService.sendRequest(
        createReqJson(uuidKey),
        uuidKey
    );
    log.debug("okMessage:");
    log.debug("  " + returnedJsonMap);
  }

  private String createReqJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    return "[\"REQ\",\"" + uuidKey + "\",{\"ids\":[\"" + uuid + "\"]}]";
  }

  private String getNextHex(int i) {
    String incrementedHexNumber = Integer.toHexString(hexStartNumber + i);
    return hexCounterSeed
        .substring(0, hexCounterSeed.length() - incrementedHexNumber.length())
        .concat(incrementedHexNumber);
  }
}
