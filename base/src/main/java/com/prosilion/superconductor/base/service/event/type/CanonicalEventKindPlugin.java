package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.base.service.request.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class CanonicalEventKindPlugin extends PublishingEventKindPlugin {

  public CanonicalEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF<Kind> eventKindPlugin) {
    super(notifierService, eventKindPlugin);
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    super.processIncomingEvent(event);
  }
}
