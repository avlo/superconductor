package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class CanonicalEventKindPlugin extends PublishingEventKindPlugin {
  public CanonicalEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindPluginIF eventKindPlugin) {
    super(notifierService, eventKindPlugin);
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    super.processIncomingEvent(event);
  }

  @Override
  public BaseEvent materialize(EventIF eventIF) {
    return new CanonicalEvent(eventIF.asGenericEventRecord());
  }

  private static class CanonicalEvent extends BaseEvent {
    public CanonicalEvent(@NonNull GenericEventRecord genericEventRecord) {
      super(genericEventRecord);
    }
  }
}
