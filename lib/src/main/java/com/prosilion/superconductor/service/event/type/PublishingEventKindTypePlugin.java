package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our CarDecorator for NonPublishingEventKindType hierarchy
public class PublishingEventKindTypePlugin implements EventKindTypePluginIF<KindTypeIF> {
  private final NotifierService notifierService;
  private final EventKindTypePluginIF<KindTypeIF> eventKindTypePlugin;

  public PublishingEventKindTypePlugin(@NonNull NotifierService notifierService, @NonNull EventKindTypePluginIF<KindTypeIF> eventKindTypePlugin) {
    this.notifierService = notifierService;
    this.eventKindTypePlugin = eventKindTypePlugin;
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
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
