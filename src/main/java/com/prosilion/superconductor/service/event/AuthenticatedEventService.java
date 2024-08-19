package com.prosilion.superconductor.service.event;

import com.prosilion.superconductor.pubsub.AddNostrEvent;
import com.prosilion.superconductor.service.NotifierService;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.TextNoteEvent;
import nostr.event.message.EventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Slf4j
@Service
public class AuthenticatedEventService<T extends EventMessage> {
  private final NotifierService<GenericEvent> notifierService;
  private final RedisCache<GenericEvent> redisCache;
  private final AuthEntityService authEntityService;

  @Autowired
  public AuthenticatedEventService(NotifierService<GenericEvent> notifierService,
      AuthEntityService authEntityService,
      RedisCache<GenericEvent> redisCache) {
    this.notifierService = notifierService;
    this.authEntityService = authEntityService;
    this.redisCache = redisCache;
  }

  //  @Async
  public void processIncomingEvent(@NonNull T eventMessage, @NonNull String sessionId) {
    log.info("processing incoming AUTHENTICATED TEXT_NOTE: [{}]", eventMessage);
    GenericEvent event = (GenericEvent) eventMessage.getEvent();

    authEntityService.findAuthEntityBySessionId(sessionId);

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
