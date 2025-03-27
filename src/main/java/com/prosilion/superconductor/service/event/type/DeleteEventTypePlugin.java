package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.entity.EventEntity;
import com.prosilion.superconductor.service.request.pubsub.AddNostrEvent;
import com.prosilion.superconductor.util.FilterMatcher;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.filter.AddressTagFilter;
import nostr.event.filter.Filters;
import nostr.event.impl.DeletionEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.tag.AddressTag;
import nostr.event.tag.EventTag;
import nostr.event.tag.GenericTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

// TODO: complete this class
@Slf4j
@Component
public class DeleteEventTypePlugin<T extends GenericEvent> extends AbstractEventTypePlugin<T> implements EventTypePlugin<T> {
  private final DeletionEventEntityService deletionEventEntityService;
  private final FilterMatcher<T> filterMatcher;

  @Autowired
  public DeleteEventTypePlugin(
      RedisCache<T> redisCache,
      DeletionEventEntityService deletionEventEntityService,
      FilterMatcher<T> filterMatcher) {
    super(redisCache);
    this.deletionEventEntityService = deletionEventEntityService;
    this.filterMatcher = filterMatcher;
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    log.debug("processing incoming DELETE EVENT: [{}]", event);

    DeletionEvent deletionEvent = new DeletionEvent(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    deletionEvent.setId(event.getId());
    deletionEvent.setCreatedAt(event.getCreatedAt());
    deletionEvent.setSignature(event.getSignature());

    save(event); // NIP-09 req's saving of event itself
    saveDeletionEvent(event);
  }

  private void saveDeletionEvent(T event) {
//    TODO: refactor below 3 sections into single stream
    List<Optional<EventEntity>> matchingEventIds = event.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(eventTag ->
            ///  debug issue this line, returns event but without BaseTags
            getRedisCache().getByEventIdString(eventTag.getIdEvent())
                .filter(eventEntity ->
                    eventEntity.getPubKey().equals(
                        event.getPubKey().toHexString()))).toList();

    List<GenericTag> kTags = event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(genericTag -> genericTag.getCode().equals("k")).toList();

    List<Optional<EventEntity>> eventsNotToFire =
        matchingEventIds.stream().map(optionalEventEntity ->
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
        optionalEventEntity.ifPresent(eventEntity -> deletionEventEntityService.addDeletionEvent(eventEntity.getId())));
  }

  private List<T> filterMatches(T event) {
    List<AddressTag> addressTagList = event.getTags().stream()
        .filter(AddressTag.class::isInstance)
        .map(AddressTag.class::cast)
        .toList();

    AddressTag first = addressTagList.getFirst();
//    Integer kind = first.getKind();
//    PublicKey pubkey = first.getPublicKey();
    String dIdent = first.getIdentifierTag().getId();

    return filterMatcher.intersectFilterMatches(
            new Filters(
                new AddressTagFilter<>(first)),
            new AddNostrEvent<T>(event)).stream()
        .map(AddNostrEvent::event).toList();
  }

  @Override
  public Kind getKind() {
    return Kind.DELETION;
  }
}

