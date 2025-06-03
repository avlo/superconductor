package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;

@Slf4j
@Getter
public abstract class AbstractPublishingEventTypePlugin<T extends GenericEvent> extends AbstractEventTypePlugin<T> {
  private final NotifierService<T> notifierService;

  public AbstractPublishingEventTypePlugin(
      @NonNull RedisCache<T> redisCache,
      @NonNull NotifierService<T> notifierService) {
    super(redisCache);
    this.notifierService = notifierService;
  }

  public void processIncomingEvent(@NonNull T event) {
    processIncomingPublishingEventType(event);
    notifierService.nostrEventHandler(new AddNostrEvent<>(event));
  }

  abstract public void processIncomingPublishingEventType(@NonNull T event);
}
