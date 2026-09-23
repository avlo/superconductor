//package com.prosilion.superconductor.base.service.event.plugin.kind;
//
//import com.prosilion.nostr.event.BaseEvent;
//import com.prosilion.nostr.event.internal.Relay;
//import com.prosilion.superconductor.base.service.event.plugin.EventPluginRxRIF;
//import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
//import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
//import java.util.Optional;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//// our CarDecorator for PublishingEventKind hierarchy
//public abstract class PublishingEventKindPluginRxR<T extends BaseEvent> implements EventKindPluginRxRIF<T> {
//  private final NotifierService notifierService;
//  private final EventPluginRxRIF<T> eventPluginIF;
//
//  public PublishingEventKindPluginRxR(@NonNull NotifierService notifierService, @NonNull EventPluginRxRIF<T> eventPluginIF) {
//    this.notifierService = notifierService;
//    this.eventPluginIF = eventPluginIF;
//  }
//
//  @Override
//  public Optional<T> processIncomingEvent(@NonNull T event, @NonNull Relay fromRelay) {
//    Optional<T> genericEventRecord = eventPluginIF.processIncomingEvent(event, fromRelay);
//    genericEventRecord.ifPresent(ger ->
//       notifierService.nostrEventHandler(new AddNostrEvent(ger)));
//    return genericEventRecord;
//  }
//}
