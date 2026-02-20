package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for PublishingEventKind hierarchy
public abstract class PublishingEventKindPlugin implements EventKindPluginIF {
  private final NotifierService notifierService;
  private final EventPlugin eventPlugin;

  public PublishingEventKindPlugin(@NonNull NotifierService notifierService, @NonNull EventPlugin eventPlugin) {
    this.notifierService = notifierService;
    this.eventPlugin = eventPlugin;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    eventPlugin.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
  }
}
