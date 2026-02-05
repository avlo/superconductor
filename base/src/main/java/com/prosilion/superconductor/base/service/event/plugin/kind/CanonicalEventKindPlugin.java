package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class CanonicalEventKindPlugin<T extends BaseEvent> extends PublishingEventKindPlugin<T> {

  public CanonicalEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF<T> eventKindPlugin) {
    super(notifierService, eventKindPlugin);
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    super.processIncomingEvent(event);
  }
}
