package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventKindIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractNonPublishingEventKindPlugin<T extends Kind> extends AbstractEventKindPlugin<T> {

  @Autowired
  public AbstractNonPublishingEventKindPlugin(@NonNull RedisCache redisCache) {
    super(redisCache);
  }

  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
//  TODO: below call may be incorrect, should discover during testing    
    processIncomingNonPublishingEventKind(event);
  }

  abstract public void processIncomingNonPublishingEventKind(@NonNull GenericEventKindIF event);
}
