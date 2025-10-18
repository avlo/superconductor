//package com.prosilion.superconductor.base.service.event.type;
//
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.enums.KindTypeIF;
//import com.prosilion.nostr.event.EventIF;
//import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
//import com.prosilion.superconductor.base.service.request.NotifierService;
//import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.lang.NonNull;
//
//@Slf4j
//// our CarDecorator for PublishingEventKindType hierarchy
//public class PublishingEventKindTypePlugin implements EventKindTypePluginIF {
//  private final NotifierService notifierService;
//  private final EventKindTypePluginIF eventKindTypePlugin;
//
//  public PublishingEventKindTypePlugin(@NonNull NotifierService notifierService, @NonNull EventKindTypePluginIF eventKindTypePlugin) {
//    this.notifierService = notifierService;
//    this.eventKindTypePlugin = eventKindTypePlugin;
//  }
//
//  @Override
//  public void processIncomingEvent(@NonNull EventIF event) {
//    eventKindTypePlugin.processIncomingEvent(event);
//    notifierService.nostrEventHandler(new AddNostrEvent(event));
//  }
//
//  @Override
//  public Kind getKind() {
//    return eventKindTypePlugin.getKind();
//  }
//
//  @Override
//  public KindTypeIF getKindType() {
//    return eventKindTypePlugin.getKindType();
//  }
//}
