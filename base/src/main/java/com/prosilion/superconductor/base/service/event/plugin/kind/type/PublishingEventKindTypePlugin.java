package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our CarDecorator for PublishingEventKindType hierarchy
public abstract class PublishingEventKindTypePlugin implements EventKindTypePluginIF {
  private final NotifierService notifierService;
  private final EventKindTypePluginIF eventKindTypePlugin;

  public PublishingEventKindTypePlugin(@NonNull NotifierService notifierService, @NonNull EventKindTypePluginIF eventKindTypePlugin) {
    this.notifierService = notifierService;
    this.eventKindTypePlugin = eventKindTypePlugin;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    Optional<GenericEventRecord> genericEventRecord = eventKindTypePlugin.processIncomingEvent(event, fromRelay);
    genericEventRecord.ifPresent(ger ->
       notifierService.nostrEventHandler(new AddNostrEvent(ger)));
    return genericEventRecord;
  }

  @Override
  public Kind getKind() {
    return eventKindTypePlugin.getKind();
  }

  @Override
  public KindTypeIF getKindType() {
    return eventKindTypePlugin.getKindType();
  }
}
