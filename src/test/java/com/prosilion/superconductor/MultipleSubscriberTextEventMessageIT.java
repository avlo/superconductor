package com.prosilion.superconductor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
class MultipleSubscriberTextEventMessageIT {
  private final ObjectMapper mapper = new ObjectMapper();

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
    this.executorService = Executors.newVirtualThreadPerTaskExecutor();
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

  @SneakyThrows
  private void createEvent(int increment) {
    String nextHex = getNextHex(increment);
    log.debug("next hex: {}", nextHex);
    String globalEventJson = getGlobalEventJson(nextHex);
    log.debug("setup() send event:\n  {}", globalEventJson);
    nostrRelayService.createEvent(globalEventJson);
  }

  private String getGlobalEventJson(String startEventId) {
    return "[ \"EVENT\", { \"content\": \"1111111111\", \"id\":\"" + startEventId + "\", \"kind\": 1, \"created_at\": 1717357053050, \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\", \"tags\": [[\"a\", \"wss://nostr.example.com\", \"30023:f7234bd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd\"], [\"custom-tag\", \"custom-tag random value\"], [\"p\", \"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"], [\"e\", \"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"], [\"g\", \"textnote geo-tag-1\"]], \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
  }

  private String getExpectedJsonInAnyOrder(String startEventId) {
    return "{\"id\":\"" + startEventId + "\", \"kind\": 1, \"created_at\": 1717357053050, \"pubkey\": \"bbbd79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\", \"tags\": [[\"a\", \"wss://nostr.example.com\", \"30023:f7234bd4c1394dda46d09f35bd384dd30cc552ad5541990f98844fb06676e9ca:abcd\"], [\"custom-tag\", \"custom-tag random value\"], [\"p\", \"2bed79f81439ff794cf5ac5f7bff9121e257f399829e472c7a14d3e86fe76984\"], [\"e\", \"494001ac0c8af2a10f60f23538e5b35d3cdacb8e1cc956fe7a16dfa5cbfc4346\"], [\"g\", \"textnote geo-tag-1\"]], \"content\": \"1111111111\", \"sig\": \"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}";
  }

  @Test
  @Order(0)
  void testReqMessageWithExecutor() {
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
    assertTrue(returnedJsonMap.get(Command.EVENT).get().contains(uuidKey));
    assertTrue(compareWithoutOrder(returnedJsonMap.get(Command.EVENT).get(), getExpectedJsonInAnyOrder(uuidKey)));
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

  private boolean compareWithoutOrder(String payloadString, String expectedJson) throws JsonProcessingException {
    JsonNode jsonNode = mapper.readTree(payloadString);
    return ComparatorWithoutOrder.equalsJson(mapper.readTree(expectedJson), jsonNode);
  }
}
