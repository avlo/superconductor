package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
@Getter
public abstract class AbstractPublishingEventKindPlugin extends AbstractEventKindPlugin {
  private final NotifierService notifierService;

  public AbstractPublishingEventKindPlugin(
      @NonNull RedisCache redisCache,
      @NonNull NotifierService notifierService) {
    super(redisCache);
    this.notifierService = notifierService;
  }

  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    processIncomingPublishingEventType(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
  }

  abstract public void processIncomingPublishingEventType(@NonNull GenericEventKindIF event);
}
