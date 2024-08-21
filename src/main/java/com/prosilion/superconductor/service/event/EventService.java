package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.NotifierService;
import lombok.NonNull;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;

public class EventService<T extends EventMessage> implements EventServiceIF<T> {
  private final NotifierService<GenericEvent> notifierService;
  private final RedisCache<GenericEvent> redisCache;

  public EventService(NotifierService<GenericEvent> notifierService, RedisCache<GenericEvent> redisCache) {
    this.notifierService = notifierService;
    this.redisCache = redisCache;
  }

  //  @Async
  public void processIncomingEvent(@NonNull T eventMessage) {
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
