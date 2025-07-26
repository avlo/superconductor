package com.prosilion.superconductor.base.service.event.type;

import com.prosilion.nostr.event.GenericEventKindIF;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.GenericTag;
import com.prosilion.superconductor.base.service.event.CacheIF;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.lang.NonNull;

public class DeleteEventPlugin implements DeleteEventPluginIF {
  private final CacheIF cacheIF;

  public DeleteEventPlugin(@NonNull CacheIF cacheIF) {
    this.cacheIF = cacheIF;
  }

  @Override
  public void processIncomingEvent(@NonNull GenericEventKindIF event) {
//    TODO: refactor below 3 sections into single stream
    List<Optional<GenericEventKindIF>> matchingEventIds = event.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(eventTag ->
            ///  TODO: check below fixed previous comment of: debug issue this line, returns event but without BaseTags
            cacheIF.getByEventIdString(eventTag.getIdEvent())
                .filter(eventEntity ->
                    eventEntity.getPublicKey().equals(
                        event.getPublicKey()))).toList();

    List<GenericTag> kTags = event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(genericTag -> genericTag.getCode().equals("k")).toList();


    List<Optional<GenericEventKindIF>> eventsNotToFire = matchingEventIds.stream().map(optionalEventEntity ->
        Optional.of(optionalEventEntity.filter(eventEntity ->
                Optional.of(kTags.stream()
                        .map(kTag ->
                            kTag.getAttributes()
                                .stream().map(elementAttribute ->
                                    elementAttribute.getValue().toString())
                                .distinct()
                        )
                        .flatMap(Stream::distinct))
                    .orElse(Stream.empty())
                    .toList()
                    .contains(eventEntity.getKind().toString())))
            .orElseGet(Optional::empty)).toList();

//    List<Long> eventsNotToFireIds = eventsNotToFire.stream().flatMap(eventEntity -> eventEntity.stream().map(EventEntity::getId)).distinct().toList();
//
//    getRedisCache().getAll().values().stream().flatMap(mapEventEntry ->
//        mapEventEntry.values().stream().map(this::filterMatches)).map(filtered -> filtered.stream().map(anitem -> anitem.)
//
//        eventsNotToFire.addAll(list.stream().map(entity -> entity.))

    eventsNotToFire.forEach(optionalEventEntity ->
        optionalEventEntity.ifPresent(cacheIF::deleteEventEntity));
  }

  //  TODO: add addyTag criterion as well to above pubkeyTag criterion
  private List<GenericEventKindIF> filterMatches(GenericEventKindIF event) {
//    List<AddressTag> addressTagList = event.getTags().stream()
//        .filter(AddressTag.class::isInstance)
//        .map(AddressTag.class::cast)
//        .toList();
//
//    AddressTag first = addressTagList.getFirst();
////    Integer kind = first.getKind();
////    PublicKey pubkey = first.getPublicKey();
//    String uuid = first.getIdentifierTag().getUuid();
//
//    return filterMatcher.intersectFilterMatches(
//            new Filters(
//                new AddressTagFilter<>(first)),
//            new AddNostrEvent<T>(event)).stream()
//        .map(AddNostrEvent::event).toList();
    return null;
  }
}
