package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for PublishingEventKind hierarchy
public class PublishingEventKindPlugin implements EventKindPluginIF<Kind> {
  private final NotifierService notifierService;
  private final EventKindPluginIF<Kind> eventKindPlugin;

  public PublishingEventKindPlugin(@NonNull NotifierService notifierService, @NonNull EventKindPluginIF<Kind> eventKindPlugin) {
    this.notifierService = notifierService;
    this.eventKindPlugin = eventKindPlugin;
  }

  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    eventKindPlugin.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
  }

  @Override
  public Kind getKind() {
    return eventKindPlugin.getKind();
  }
}
