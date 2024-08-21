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
    havingValue = "false")
public class EventServiceNoAuthDecorator<T extends EventMessage> implements EventServiceIF<T> {
  private final EventService<T> eventService;

  @Autowired
  public EventServiceNoAuthDecorator(NotifierService<GenericEvent> notifierService, RedisCache<GenericEvent> redisCache) {
    log.info("creating UN-wAUTHENTICATED event service");
    this.eventService = new EventService<>(notifierService, redisCache);
  }

  //  @Async
  public void processIncomingEvent(@NonNull T eventMessage) {
    log.info("processing incoming TEXT_NOTE: [{}]", eventMessage);
    eventService.processIncomingEvent(eventMessage);
  }
}
