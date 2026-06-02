package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.codec.BaseMessageDecoder;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.message.BaseMessage;
import com.prosilion.nostr.message.EventMessage;
import com.prosilion.nostr.message.ReqMessage;
import com.prosilion.nostr.util.Util;
import com.prosilion.subdivisions.client.RequestSubscriber;
import com.prosilion.subdivisions.client.virtualthread.VThreadWebSocketClient;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.BooleanSupplier;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RemoteAbstractTagService {
  static int padding = 4;
  private final static String blankPadding = " ".repeat(padding);

  protected List<GenericEventRecord> sendRemoteReq(String relayUrl, Filters apply) {
    ReqMessage reqMessage = new ReqMessage(Util.generateRandomHex64String(), apply);

    String lineBreak = "\n";
    log.debug("... sendConsolidatorReq() (1 of 3) sending request message to:\nURL:  {}\nusing subscriberId:  [{}]\nand filters:\n  {}",
        relayUrl,
        reqMessage.getSubscriptionId(),
        apply.toString().replace(lineBreak, Strings.concat(lineBreak, RemoteAbstractTagService.blankPadding))
    );

//    TODO: finalize which awaitXXX() variant given below awaitXXX() options
    List<BaseMessage> eventList = awaitUsingWebSocketClient(reqMessage, relayUrl);
//    List<BaseMessage> eventList = awaitUsingCompletableFuture(reqMessage, relayUrl);

    log.debug("... sendConsolidatorReq() (2 of 3) retrieved results...");
    List<GenericEventRecord> events = getGenericEvents(eventList);
    log.debug("... sendConsolidatorReq() (3 of 3) returning results:\n  {}",
        events.stream().map(GenericEventRecord::createPrettyPrintJson).map(s -> Strings.concat("SUCCESS:\n  ", s)));
    return events;
  }

  private List<GenericEventRecord> getGenericEvents(List<BaseMessage> returnedBaseMessages) {
    List<GenericEventRecord> genericEventRecords = returnedBaseMessages.stream()
        .filter(EventMessage.class::isInstance)
        .map(EventMessage.class::cast)
        .map(EventMessage::getEvent)
        .map(EventIF::asGenericEventRecord).toList();
    log.debug("getGenericEvents(List<BaseMessage> returnedBaseMessages) returned:\n{}",
        Util.prettyPrintGenericEventRecords(genericEventRecords));
    return genericEventRecords;
  }

  private List<BaseMessage> awaitUsingWebSocketClient(ReqMessage reqMessage, String relayUrl) {
//  new WebSocketClient(relayUrl);
    try {
      VThreadWebSocketClient vThreadWebSocketClient = new VThreadWebSocketClient(relayUrl);
      vThreadWebSocketClient.send(reqMessage);
      BooleanSupplier b = () -> !vThreadWebSocketClient.getEvents().isEmpty();
      RequestSubscriber.await(Duration.ofSeconds(10), b);
      List<String> populatedEvents = vThreadWebSocketClient.getPopulatedEvents();
      List<BaseMessage> list = populatedEvents.stream().map(msg -> {
        try {
          return BaseMessageDecoder.decode(msg);
        } catch (JsonProcessingException e) {
          throw new RuntimeException(e);
        }
      }).toList();
      return list;
    } catch (ExecutionException | InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

//  private List<BaseMessage> awaitUsingNostrSingleRequestServiceExplicitSubscriber(ReqMessage reqMessage, String relayUrl) {
//    log.debug("awaitUsingNostrSingleRequestServiceExplicitSubscriber (1of2) new NostrSingleRequestService().send(reqMessage, relayUrl, requestTimeoutDuration (5 seconds))...");
//
//    Hooks.onOperatorDebug();
//
//    RequestSubscriber<BaseMessage> remoteGenericEventRecordSubscriber = new RequestSubscriber<>(
//        Duration.ofSeconds(10));
//
//    new NostrSingleRequestService().send(
//        reqMessage,
//        relayUrl,
//        remoteGenericEventRecordSubscriber);
//    log.debug("... remoteGenericEventRecordSubscriber.getAreItemsPopulated()... {}", remoteGenericEventRecordSubscriber.getAreItemsPopulated());
//    List<BaseMessage> baseMessages = remoteGenericEventRecordSubscriber.getItems();
//    remoteGenericEventRecordSubscriber.dispose();
//    log.debug("... remoteGenericEventRecordSubscriber.dispose() ...");
//
//    log.debug("awaitUsingNostrSingleRequestService (2of2) ... complete. returned baseMessages (count {}):", baseMessages.size());
//    baseMessages.forEach(RemoteAbstractTagService::debugPrintBaseMessage);
//    return baseMessages;
//  }

//  private List<BaseMessage> awaitUsingNostrSingleRequestServiceImplicitSubscriber(ReqMessage reqMessage, String relayUrl) {
//    log.debug("awaitUsingNostrSingleRequestService (1of2) new NostrSingleRequestService().send(reqMessage, relayUrl, requestTimeoutDuration)...");
//    NostrSingleRequestService nostrSingleRequestService = new NostrSingleRequestService();
//    List<BaseMessage> baseMessages = nostrSingleRequestService.send(reqMessage, relayUrl, Duration.of(10, ChronoUnit.SECONDS));
//    log.debug("awaitUsingNostrSingleRequestService (2of2) ... complete. returned baseMessages (count {}):", baseMessages.size());
//    baseMessages.forEach(RemoteAbstractTagService::debugPrintBaseMessage);
//    return baseMessages;
//  }

//  private List<BaseMessage> awaitUsingRequestSubscriberFxns(ReqMessage reqMessage, String relayUrl) {
//    log.debug("awaitUsingRequestSubscriberFxns (0of5) inside await()...");
//    RequestSubscriber<BaseMessage> subscriber = new RequestSubscriber<>();
//    NostrSingleRequestService nostrSingleRequestService = new NostrSingleRequestService();
//    log.debug("awaitUsingRequestSubscriberFxns (1of5) ... calling nostrSingleRequestService.send(reqMessage, relayUrl, subscriber) using ReqMessage...");
//    debugPrintBaseMessage(reqMessage);
//    nostrSingleRequestService.send(reqMessage, relayUrl, subscriber);
//    log.debug("awaitUsingRequestSubscriberFxns (2of5) ... done, calling subscriber.getItems() ...");
//    List<BaseMessage> baseMessages = subscriber.getItems();
//    log.debug("awaitUsingRequestSubscriberFxns (3of5) ... done, calling subscriber.dispose() ...");
//    subscriber.dispose();
//    log.debug("awaitUsingRequestSubscriberFxns (4of5) ... done, calling manager.closeAllSessions() ...");
//    log.debug("awaitUsingRequestSubscriberFxns (5of5) ... complete. returned baseMessages (count {}):", baseMessages.size());
//    baseMessages.forEach(RemoteAbstractTagService::debugPrintBaseMessage);
//    return baseMessages;
//  }

//  private List<BaseMessage> awaitUsingCompletableFuture(ReqMessage reqMessage, String relayUrl) {
//    log.debug("awaitUsingCompletableFuture (0of5) inside awaitRxR()...");
//    RequestSubscriber<BaseMessage> subscriber = new RequestSubscriber<>();
//
//    CompletableFuture<Void> voidCompletableFutureNostrRequestService = CompletableFuture.runAsync(() ->
//            new NostrSingleRequestService().send(reqMessage, relayUrl, subscriber)
//        , Executors.newVirtualThreadPerTaskExecutor());
//    log.debug("awaitUsingCompletableFuture (1of5) voidCompletableFutureNostrRequestService send() checkpoint");
//
//    RequestSubscriber.await(
//        Duration.of(5000, ChronoUnit.MILLIS),
//        voidCompletableFutureNostrRequestService::isDone);
//    log.debug("awaitUsingCompletableFuture (2of5) voidCompletableFutureNostrRequestService isDone checkpoint");
//
//    CompletableFuture<List<BaseMessage>> getBaseMessagesCompletableFuture = CompletableFuture.supplyAsync(() ->
//            subscriber.getItems()
//        , Executors.newVirtualThreadPerTaskExecutor());
//    log.debug("awaitUsingCompletableFuture (3of5) getBaseMessagesCompletableFuture eventList.set(subscriber.getItems()) checkpoint");
//
//    List<BaseMessage> baseMessages = null;
//    getBaseMessagesCompletableFuture.thenApply(baseMessagesList ->
//        baseMessages.addAll(baseMessagesList)).join();
//    log.debug("awaitUsingCompletableFuture (4of5) getBaseMessagesCompletableFuture isDone checkpoint");
//
//    subscriber.dispose();
//    log.debug("awaitUsingCompletableFuture (5of5) returning baseMessages:");
//    baseMessages.forEach(baseMessage -> log.debug("  " + baseMessage.toString()));
//    return baseMessages;
//  }

//  private static void debugPrintBaseMessage(BaseMessage baseMessage) {
//    String encode;
//    try {
//      encode = baseMessage.encode();
//      log.debug(encode);
//    } catch (JsonProcessingException e) {
//      log.debug("printDebug(BaseMessage baseMessage) shit the bed, just print baseMessage");
//      log.debug(baseMessage.toString());
//    }
//  }
}
