package com.prosilion.superconductor;

import com.google.common.util.concurrent.MoreExecutors;
import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;

@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext
@ActiveProfiles("test")
@TestMethodOrder(OrderAnnotation.class)
class VirtualThreadsVsGuavaMoreExecutorsDurationComparisionIT {
  private final String hexCounterSeed;
  private final int hexStartNumber;
  private final Integer targetCount;

  private final NostrRelayService nostrRelayService;
  private final String uuidPrefix;

  private ExecutorService executorService;

  @Autowired
  VirtualThreadsVsGuavaMoreExecutorsDurationComparisionIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
    this.nostrRelayService = nostrRelayService;
    this.uuidPrefix = uuidPrefix;
    this.hexCounterSeed = hexCounterSeed.repeat(2*hexNumberOfBytes);
    this.hexStartNumber = Integer.parseInt(hexCounterSeed, 16);
    this.targetCount = reqInstances;
  }

  @BeforeEach
  public void setup(TestInfo testInfo) throws IOException {
    long start = System.currentTimeMillis();

    String methodName = testInfo.getTestMethod().get().getName();
    if (methodName.equals("testVirtualThreadExecutor")) {
      executorService = Executors.newVirtualThreadPerTaskExecutor();
    } else {
      executorService = MoreExecutors.newDirectExecutorService();
    }

    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
        IntStream.range(0, targetCount).forEach(this::createEvent), executorService);
    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(voidCompletableFuture::isDone);

    log.debug("events setup elapsed time [{}]", System.currentTimeMillis() - start);
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
  public void testVirtualThreadExecutor() {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> requestFutures = CompletableFuture.runAsync(() ->
        IntStream.range(0, targetCount).forEach(this::sendRequest), executorService);
    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(requestFutures::isDone);

    log.debug("jdk virtual threads requests completed after elapsed time [{}]", System.currentTimeMillis() - start);
  }

  @Test
  @Order(1)
  public void testGuavaDirectThreadExecutor() {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> requestFutures = CompletableFuture.runAsync(() ->
        IntStream.range(0, targetCount).forEach(this::sendRequest), executorService);
    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(requestFutures::isDone);

    log.debug("guava MoreExecutors requests completed after elapsed time [{}]", System.currentTimeMillis() - start);
  }

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
    return "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
  }

  private String getNextHex(int i) {
    String incrementedHexNumber = Integer.toHexString(hexStartNumber + i);
    log.debug("incrementedHexNumber:\n  [{}]", incrementedHexNumber);
    return hexCounterSeed
        .substring(0, hexCounterSeed.length() - incrementedHexNumber.length())
        .concat(incrementedHexNumber);
  }
}
