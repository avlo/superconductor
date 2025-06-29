package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.event.service.plugin.EventKindPluginIF;
import com.prosilion.superconductor.service.request.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CanonicalEventKindPlugin extends PublishingEventKindPlugin {

  public CanonicalEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF<Kind> eventKindPlugin) {
    super(notifierService, eventKindPlugin);
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    super.processIncomingEvent(event);
  }
}
