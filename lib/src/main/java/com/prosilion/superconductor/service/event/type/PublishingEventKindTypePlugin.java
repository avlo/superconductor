package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.KindTypeIF;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.event.GenericEventKindTypeIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePlugin;
import com.prosilion.superconductor.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// DECORATOR
public class PublishingEventKindTypePlugin extends EventKindTypePlugin {
  private final NotifierService notifierService;

  public PublishingEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindTypePluginIF<KindTypeIF> eventKindTypePlugin) {
    super(eventKindTypePlugin.getKindType(), eventKindTypePlugin);
    this.notifierService = notifierService;
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    super.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindTypeIF event) {
    super.processIncomingEvent(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
  }
}
