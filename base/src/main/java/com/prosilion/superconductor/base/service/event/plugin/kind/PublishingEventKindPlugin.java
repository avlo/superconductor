package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for PublishingEventKind hierarchy
public abstract class PublishingEventKindPlugin implements EventKindPluginIF {
  private final NotifierService notifierService;
  private final EventPluginIF eventPluginIF;

  public PublishingEventKindPlugin(@NonNull NotifierService notifierService, @NonNull EventPluginIF eventPluginIF) {
    this.notifierService = notifierService;
    this.eventPluginIF = eventPluginIF;
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    GenericEventRecord genericEventRecord = eventPluginIF.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
    return genericEventRecord;
  }
}
