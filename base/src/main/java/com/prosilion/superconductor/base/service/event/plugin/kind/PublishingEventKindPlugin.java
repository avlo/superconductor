package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay relay) {
    Optional<GenericEventRecord> genericEventRecord = eventPluginIF.processIncomingEvent(event, relay);
    genericEventRecord.ifPresent(ger ->
       notifierService.nostrEventHandler(new AddNostrEvent(ger)));
    return genericEventRecord;
  }
}
