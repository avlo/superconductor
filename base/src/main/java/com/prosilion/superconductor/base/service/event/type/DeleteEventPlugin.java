package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public class DeleteEventPlugin implements DeleteEventPluginIF {
  private final CacheServiceIF cacheServiceIF;

  public DeleteEventPlugin(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
//    List<GenericTag> kTags = event.getTags().stream()
//        .filter(GenericTag.class::isInstance)
//        .map(GenericTag.class::cast)
//        .filter(genericTag -> genericTag.getCode().equals("k")).toList();
//
//
//    List<Optional<GenericEventKindIF>> eventsNotToFire = matchingEventIds.stream().map(optionalEventEntity ->
//        Optional.of(optionalEventEntity.filter(eventEntity ->
//                Optional.of(kTags.stream()
//                        .map(kTag ->
//                            kTag.getAttributes()
//                                .stream().map(elementAttribute ->
//                                    elementAttribute.getValue().toString())
//                                .distinct()
//                        )
//                        .flatMap(Stream::distinct))
//                    .orElse(Stream.empty())
//                    .toList()
//                    .contains(eventEntity.getKind().toString())))
//            .orElseGet(Optional::empty)).toList();

    List<? extends EventIF> list = event.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(eventTag ->
        {
          String idEvent = eventTag.getIdEvent();
          Optional<? extends EventIF> byEventIdString = cacheServiceIF.getEventByEventId(idEvent);
          return byEventIdString
              .filter(cacheEvent ->
                  cacheEvent.getPublicKey().toString().equals(
                      event.getPublicKey().toString()));
        }).flatMap(Optional::stream).toList();

    list.forEach(this::getAVoid);
  }

  private void getAVoid(EventIF genericEventKindIF) {
    cacheServiceIF.deleteEventEntity(genericEventKindIF);
  }
}
