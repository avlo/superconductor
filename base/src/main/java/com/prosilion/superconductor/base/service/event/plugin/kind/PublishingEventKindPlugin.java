package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for PublishingEventKind hierarchy
public abstract class PublishingEventKindPlugin implements EventKindPluginIF {
  private final NotifierService notifierService;
  private final EventKindPluginIF eventKindPlugin;

  public PublishingEventKindPlugin(@NonNull NotifierService notifierService, @NonNull EventKindPluginIF eventKindPlugin) {
    this.notifierService = notifierService;
    this.eventKindPlugin = eventKindPlugin;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    eventKindPlugin.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
  }

  @Override
  public Kind getKind() {
    return eventKindPlugin.getKind();
  }
}
