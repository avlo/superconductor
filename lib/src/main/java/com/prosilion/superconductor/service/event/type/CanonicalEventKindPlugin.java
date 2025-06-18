package com.prosilion.superconductor.service.event.type;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventDtoIF;
import com.prosilion.superconductor.service.request.NotifierService;
import org.springframework.lang.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CanonicalEventKindPlugin<T extends GenericEventDtoIF> extends AbstractPublishingEventKindPlugin<T> {


  @Autowired
  public CanonicalEventKindPlugin(@NonNull RedisCache<T> redisCache, @NonNull NotifierService<T> notifierService) {
    super(redisCache, notifierService);
  }

  @Override
  public void processIncomingPublishingEventType(@NonNull T event) {
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
