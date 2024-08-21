package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.NotifierService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventService<T extends EventMessage> implements EventServiceIF<T> {
  private final NotifierService<GenericEvent> notifierService;
  private final RedisCache<GenericEvent> redisCache;

  @Autowired
  public EventService(NotifierService<GenericEvent> notifierService, RedisCache<GenericEvent> redisCache) {
    this.notifierService = notifierService;
    this.redisCache = redisCache;
  }

  //  @Async
  public void processIncomingEvent(@NonNull T eventMessage) {
    log.info("processing incoming TEXT_NOTE: [{}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();

    TextNoteEvent textNoteEvent = new TextNoteEvent(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    textNoteEvent.setId(event.getId());
    textNoteEvent.setCreatedAt(event.getCreatedAt());
    textNoteEvent.setSignature(event.getSignature());

    redisCache.saveEventEntity(event);
    notifierService.nostrEventHandler(new AddNostrEvent<>(event));
  }
}
