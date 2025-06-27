package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.superconductor.service.request.NotifierService;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractPublishingEventKindPlugin<T extends Kind> extends AbstractEventKindPlugin<T> {
  private final NotifierService notifierService;

  @Autowired
  public AbstractPublishingEventKindPlugin(
      @NonNull RedisCache redisCache,
      @NonNull NotifierService notifierService) {
    super(redisCache);
    this.notifierService = notifierService;
  }

  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
    processIncomingPublishingEventKind(event);
    notifierService.nostrEventHandler(new AddNostrEvent(event));
  }

  abstract public void processIncomingPublishingEventKind(@NonNull GenericEventKindIF event);
}
