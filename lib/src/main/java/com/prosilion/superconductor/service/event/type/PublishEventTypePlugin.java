package com.prosilion.superconductor.service.event.type;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.GenericEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PublishEventTypePlugin<T extends GenericEvent> extends AbstractEventTypePlugin<T> implements EventTypePlugin<T> {

  @Autowired
  public PublishEventTypePlugin(@NonNull RedisCache<T> redisCache) {
    super(redisCache);
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    log.debug("processing incoming CANONICAL EVENT: [{}]", event);
//    TODO: below necessary/useful?
//    TextNoteEvent textNoteEvent = new TextNoteEvent(
//        event.getPubKey(),
//        event.getTags(),
//        event.getContent()
//    );
//    textNoteEvent.setId(event.getId());
//    textNoteEvent.setCreatedAt(event.getCreatedAt());
//    textNoteEvent.setSignature(event.getSignature());
    save(event);
  }

  @Override
  public Kind getKind() {
    return Kind.TEXT_NOTE;
  }
}
