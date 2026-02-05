package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for PublishingEventKindType hierarchy
public class PublishingEventKindTypePlugin<T extends BaseEvent> implements EventKindTypePluginIF<T> {
  private final NotifierService notifierService;
  private final EventKindTypePluginIF<T> eventKindTypePlugin;

  public PublishingEventKindTypePlugin(@NonNull NotifierService notifierService, @NonNull EventKindTypePluginIF<T> eventKindTypePlugin) {
    this.notifierService = notifierService;
    this.eventKindTypePlugin = eventKindTypePlugin;
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    eventKindTypePlugin.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
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
