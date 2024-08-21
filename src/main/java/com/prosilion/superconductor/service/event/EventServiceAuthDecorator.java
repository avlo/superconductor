package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.service.NotifierService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    name = "superconductor.auth.active",
    havingValue = "true")
public class EventServiceAuthDecorator<T extends EventMessage> {
  private final EventService<T> eventService;
  private final AuthEntityService authEntityService;

  @Autowired
  public EventServiceAuthDecorator(NotifierService<GenericEvent> notifierService,
      AuthEntityService authEntityService,
      RedisCache<GenericEvent> redisCache) {
    log.info("creating AUTHENTICATED event service");
    this.eventService = new EventService<>(notifierService, redisCache);
    this.authEntityService = authEntityService;
  }

  //  @Async
  public void processIncomingEvent(@NonNull T eventMessage, @NonNull String sessionId) {
    log.info("processing incoming AUTHENTICATED TEXT_NOTE: [{}]", eventMessage);
    authEntityService.findAuthEntityBySessionId(sessionId).orElseThrow();
    eventService.processIncomingEvent(eventMessage);
  }
}
