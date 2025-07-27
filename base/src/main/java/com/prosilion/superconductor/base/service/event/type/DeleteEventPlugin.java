package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.event.CacheIF;
import org.springframework.lang.NonNull;

public class DeleteEventPlugin implements DeleteEventPluginIF {
  private final CacheIF cacheIF;

  public DeleteEventPlugin(@NonNull CacheIF cacheIF) {
    this.cacheIF = cacheIF;
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
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

    event.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(eventTag ->
            cacheIF.getByEventIdString(eventTag.getIdEvent())
                .filter(eventEntity ->
                    eventEntity.getPublicKey().equals(
                        event.getPublicKey()))).toList().forEach(optionalEventEntity ->
            optionalEventEntity.ifPresent(cacheIF::deleteEventEntity));
  }
}
