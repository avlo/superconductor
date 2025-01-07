package com.prosilion.superconductor;

import com.prosilion.superconductor.util.NostrRelayService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.base.Command;
import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@Nested
class MultipleSubscriberEventIdAndAuthorIT extends AbstractMultipleSubscriber {
  private final String uuidPrefix;

  @Autowired
  MultipleSubscriberEventIdAndAuthorIT(
      @NonNull NostrRelayService nostrRelayService,
      @Value("${superconductor.test.subscriberid.prefix}") String uuidPrefix,
      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
    super(nostrRelayService, hexCounterSeed, hexNumberOfBytes, reqInstances);
    this.uuidPrefix = uuidPrefix;
  }


  @Test
  @Order(1)
  void testAuthorRequestfromItsOwnTest() {
    long start = System.currentTimeMillis();

    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
            IntStream.range(0, super.getTargetCount()).forEach(value ->
                assertAll(() -> sendRequestForAuthor()))
        , super.getExecutorService());

    await()
        .timeout(1, TimeUnit.MINUTES)
        .until(voidCompletableFuture::isDone);

    assertFalse(voidCompletableFuture.isCompletedExceptionally());

    log.debug("testReqMessageWithExecutor requests completed after elapsed time [{}]", System.currentTimeMillis() - start);
  }

  private void sendRequestForAuthor() throws IOException, ExecutionException, InterruptedException {
    String increment = super.getHexCounterSeed();

    Map<Command, Optional<String>> authorMap = super.getNostrRelayService().sendRequest(
        createReqAuthorJson(increment),
        increment
    );

    log.debug("author okMessage:");
    log.debug("  " + authorMap);
    String responseJson = authorMap.get(Command.EVENT).get();
    String expectedJsonInAnyOrder = getExpectedJsonInAnyOrder(increment);
    log.debug("author expectedJson:\n  {}", expectedJsonInAnyOrder);
    log.debug("------------");
    log.debug("author responseJson:\n  {}", responseJson);
    assertTrue(responseJson.contains(increment));
    assertTrue(super.compareWithoutOrder(responseJson, expectedJsonInAnyOrder));
  }

  public String createReqJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    String reqJson = "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
    log.debug("generated EVENT request json:\n  {}", reqJson);
    return reqJson;
  }

  private String createReqAuthorJson(@NonNull String uuid) {
    final String uuidKey = Strings.concat(uuidPrefix, uuid);
    String reqJson = "[\"REQ\",\"" + uuid + "\",{\"authors\":[\"" + uuid + "\"]}]";
    log.debug("generated AUTHOR request json:\n  {}", reqJson);
    return reqJson;
  }

  public String getGlobalEventJson(String startEventId) {
    return "[\"EVENT\",{\"id\":\"" + startEventId + "\",\"kind\":1,\"content\":\"1111111111\",\"pubkey\":\"" + startEventId + "\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
  }

  public String getExpectedJsonInAnyOrder(String startEventId) {
    return "{\"id\":\"" + startEventId + "\",\"kind\":1,\"content\":\"1111111111\",\"pubkey\":\"" + startEventId + "\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}";
  }
}
