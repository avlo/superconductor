//package com.prosilion.superconductor.service.event.type;
//
//import prosilion.superconductor.lib.jpa.entity.EventEntity;
//import com.prosilion.superconductor.base.request.service.NotifierService;
//import com.prosilion.superconductor.base.pubsub.request.service.AddNostrEvent;
//import com.prosilion.superconductor.base.util.FilterMatcher;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Stream;
//import org.springframework.lang.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import com.prosilion.nostr.enums.Kind;
//import com.prosilion.nostr.filter.tag.AddressTagFilter;
//import com.prosilion.nostr.filter.Filters;
//import com.prosilion.nostr.tag.AddressTag;
//import com.prosilion.nostr.tag.EventTag;
//import com.prosilion.nostr.tag.GenericTag;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Slf4j
//@Component
//public class DeleteEventKindPlugin<T extends DeletionEvent> extends AbstractPublishingEventKindPlugin<T> {
//  private final DeletionEventEntityService deletionEventEntityService;
//  private final FilterMatcher<T> filterMatcher;
//
//  @Autowired
//  public DeleteEventKindPlugin(
//      @NonNull RedisCache<T> redisCache,
//      @NonNull NotifierService<T> notifierService,
//      @NonNull DeletionEventEntityService deletionEventEntityService,
//      @NonNull FilterMatcher<T> filterMatcher) {
//    super(redisCache, notifierService);
//    this.deletionEventEntityService = deletionEventEntityService;
//    this.filterMatcher = filterMatcher;
//  }
//
//  @Override
//  public void processIncomingPublishingEventType(@NonNull T event) {
//    log.debug("processing incoming DELETE EVENT: [{}]", event);
//    save(event); // NIP-09 req's saving of event itself
//    saveDeletionEvent(event);
//  }
//
//  private void saveDeletionEvent(T event) {
////    TODO: refactor below 3 sections into single stream
//    List<Optional<EventEntity>> matchingEventIds = event.getTags().stream()
//        .filter(EventTag.class::isInstance)
//        .map(EventTag.class::cast)
//        .map(eventTag ->
//            ///  debug issue this line, returns event but without BaseTags
//            getRedisCache().getByEventIdString(eventTag.getIdEvent())
//                .filter(eventEntity ->
//                    eventEntity.getPubKey().equals(
//                        event.getPubKey().toHexString()))).toList();
//
//    List<GenericTag> kTags = event.getTags().stream()
//        .filter(GenericTag.class::isInstance)
//        .map(GenericTag.class::cast)
//        .filter(genericTag -> genericTag.getCode().equals("k")).toList();
//
//    List<Optional<EventEntity>> eventsNotToFire =
//        matchingEventIds.stream().map(optionalEventEntity ->
//            Optional.of(optionalEventEntity.filter(eventEntity ->
//                    Optional.of(kTags.stream()
//                            .map(kTag ->
//                                kTag.getAttributes()
//                                    .stream().map(elementAttribute ->
//                                        elementAttribute.getValue().toString())
//                                    .distinct()
//                            )
//                            .flatMap(Stream::distinct))
//                        .orElse(Stream.empty())
//                        .toList()
//                        .contains(eventEntity.getKind().toString())))
//                .orElseGet(Optional::empty)).toList();
//
////    List<Long> eventsNotToFireIds = eventsNotToFire.stream().flatMap(eventEntity -> eventEntity.stream().map(EventEntity::getId)).distinct().toList();
////
////    getRedisCache().getAll().values().stream().flatMap(mapEventEntry ->
////        mapEventEntry.values().stream().map(this::filterMatches)).map(filtered -> filtered.stream().map(anitem -> anitem.)
////
////        eventsNotToFire.addAll(list.stream().map(entity -> entity.))
//
//    eventsNotToFire.forEach(optionalEventEntity ->
//        optionalEventEntity.ifPresent(eventEntity -> deletionEventEntityService.addDeletionEvent(eventEntity.getId())));
//  }
//
//  private List<T> filterMatches(T event) {
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
//  }
//
//  @Override
//  public Kind getKind() {
//    return Kind.DELETION;
//  }
//}
//
