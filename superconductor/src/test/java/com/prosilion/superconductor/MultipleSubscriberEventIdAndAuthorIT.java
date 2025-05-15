//package com.prosilion.superconductor;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.prosilion.superconductor.util.Factory;
//import com.prosilion.superconductor.util.NostrRelayService;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.IntStream;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import nostr.base.PublicKey;
//import nostr.event.BaseMessage;
//import nostr.event.filter.AuthorFilter;
//import nostr.event.filter.Filters;
//import nostr.event.impl.GenericEvent;
//import nostr.event.message.EoseMessage;
//import nostr.event.message.ReqMessage;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Order;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import static com.prosilion.superconductor.EventMessageIT.getGenericEvents;
//import static org.awaitility.Awaitility.await;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@Slf4j
//@Nested
//class MultipleSubscriberEventIdAndAuthorIT extends AbstractMultipleSubscriber {
//  private final String authorPubKey;
//  private final String content;
//
//  @Autowired
//  MultipleSubscriberEventIdAndAuthorIT(
//      @NonNull NostrRelayService nostrRelayService,
//      @Value("${superconductor.test.req.hexCounterSeed}") String hexCounterSeed,
//      @Value("${superconductor.test.req.hexNumberOfBytes}") Integer hexNumberOfBytes,
//      @Value("${superconductor.test.req.instances}") Integer reqInstances) {
//    super(nostrRelayService, hexCounterSeed, hexNumberOfBytes, reqInstances);
//    this.authorPubKey = Factory.generateRandomHex64String();
//    this.content = Factory.lorumIpsum(getClass());
//  }
//
//  @Test
//  @Order(99)
//  void testAuthorRequestfromItsOwnTest() {
//    CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() ->
//            IntStream.range(0, getTargetEventIds().size()).forEach(value ->
//                assertAll(() -> sendRequestForAuthor(getTargetEventIds().get(value))))
//        , super.getExecutorService());
//
//    await()
//        .timeout(1, TimeUnit.MINUTES)
//        .until(voidCompletableFuture::isDone);
//
//    assertFalse(voidCompletableFuture.isCompletedExceptionally());
//  }
//
//  private void sendRequestForAuthor(String uuid) throws JsonProcessingException {
//    final String subscriberId = Factory.generateRandomHex64String();
//
//    PublicKey publicKey = new PublicKey(uuid);
//    AuthorFilter<PublicKey> authorFilter = new AuthorFilter<>(publicKey);
//
//    ReqMessage reqMessage = new ReqMessage(subscriberId, new Filters(authorFilter));
//    List<BaseMessage> returnedBaseMessages = super.getNostrRelayService().send(reqMessage);
//    List<GenericEvent> returnedEvents = getGenericEvents(returnedBaseMessages);
//
//    assertEquals(2, returnedBaseMessages.size());
//    assertTrue(returnedBaseMessages
//        .stream()
//        .filter(EoseMessage.class::isInstance)
//        .map(EoseMessage.class::cast)
//        .findAny().isPresent());
//
//    assertTrue(returnedEvents.stream().anyMatch(event -> event.getPubKey().toHexString().equals(publicKey.toHexString())));
//
//    String responseJson = returnedEvents.stream().map(event -> getExpectedJsonInAnyOrder(event.getId())).findFirst().orElseThrow();
//    String expectedJsonInAnyOrder = getExpectedJsonInAnyOrder(authorPubKey);
//    log.debug("author expectedJson:\n  {}", expectedJsonInAnyOrder);
//    log.debug("------------");
//    assertTrue(responseJson.contains(authorPubKey));
//    assertTrue(compareWithoutOrder(responseJson, expectedJsonInAnyOrder));
//  }
//
//  public String createReqJson(@NonNull String uuid) {
//    String reqJson = "[\"REQ\",\"" + uuid + "\",{\"ids\":[\"" + uuid + "\"]}]";
//    log.debug("generated EVENT request json:\n  {}", reqJson);
//    return reqJson;
//  }
//
//  public String getGlobalEventJson(String startEventId) {
//    return "[\"EVENT\",{\"id\":\"" + startEventId + "\",\"kind\":1,\"content\":\"" + content + "\",\"pubkey\":\"" + authorPubKey + "\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}]";
//  }
//
//  public String getExpectedJsonInAnyOrder(String startEventId) {
//    return "{\"id\":\"" + startEventId + "\",\"kind\":1,\"content\":\"" + content + "\",\"pubkey\":\"" + authorPubKey + "\",\"created_at\":1717357053050,\"tags\":[],\"sig\":\"86f25c161fec51b9e441bdb2c09095d5f8b92fdce66cb80d9ef09fad6ce53eaa14c5e16787c42f5404905536e43ebec0e463aee819378a4acbe412c533e60546\"}";
//  }
//}
