package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.Getter;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public abstract class AbstractPublishingEventKindPlugin<T extends GenericEventKindIF> extends AbstractEventKindPlugin<T> {
  private final NotifierService<T> notifierService;

  public AbstractPublishingEventKindPlugin(
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
