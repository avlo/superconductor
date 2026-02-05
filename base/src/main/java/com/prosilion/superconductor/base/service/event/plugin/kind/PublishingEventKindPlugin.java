package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for PublishingEventKind hierarchy
public class PublishingEventKindPlugin<T extends BaseEvent> implements EventKindPluginIF<T> {
  private final NotifierService notifierService;
  private final EventKindPluginIF<T> eventKindPlugin;

  public PublishingEventKindPlugin(@NonNull NotifierService notifierService, @NonNull EventKindPluginIF<T> eventKindPlugin) {
    this.notifierService = notifierService;
    this.eventKindPlugin = eventKindPlugin;
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    eventKindPlugin.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
  }

  @Override
  public Kind getKind() {
    return eventKindPlugin.getKind();
  }
}
