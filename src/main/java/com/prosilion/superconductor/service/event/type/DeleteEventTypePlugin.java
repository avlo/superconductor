package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.entity.EventEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.impl.DeletionEvent;
import nostr.event.impl.GenericEvent;
import nostr.event.impl.GenericTag;
import nostr.event.tag.EventTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
public class DeleteEventTypePlugin<T extends GenericEvent> implements EventTypePlugin<T> {
  private final RedisCache<T> redisCache;

  @Autowired
  public DeleteEventTypePlugin(RedisCache<T> redisCache) {
    this.redisCache = redisCache;
  }

  @Override
  public void processIncomingEvent(@NonNull T event) {
    log.debug("deleting incoming TEXT_NOTE: [{}]", event);

    DeletionEvent deletionEvent = new DeletionEvent(
        event.getPubKey(),
        event.getTags(),
        event.getContent()
    );
    deletionEvent.setId(event.getId());
    deletionEvent.setCreatedAt(event.getCreatedAt());
    deletionEvent.setSignature(event.getSignature());

    saveEvent(event);
    deleteEvents(event);
  }

  private void saveEvent(T event) {
    redisCache.saveEventEntity(event); // NIP-09 req's saving of event itself
  }

  private void deleteEvents(T event) {
//    TODO: refactor below 3 sections into single stream
    List<Optional<EventEntity>> matchingEvents = event.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(eventTag ->


            ///  debug issue this line, returns event but without BaseTags
            redisCache.getByEventIdString(eventTag.getIdEvent())



                .filter(eventEntity ->
                    eventEntity.getPubKey().equals(
                        event.getPubKey().toHexString()))).toList();

    List<GenericTag> kTags = event.getTags().stream()
        .filter(GenericTag.class::isInstance)
        .map(GenericTag.class::cast)
        .filter(genericTag -> genericTag.getCode().equals("k")).toList();

    List<Optional<EventEntity>> eventsToDelete =
        matchingEvents.stream().map(optionalEventEntity ->
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

    eventsToDelete.forEach(optionalEventEntity ->
        optionalEventEntity.ifPresent(redisCache::deleteEventEntity));
  }

  @Override
  public Kind getKind() {
    return Kind.DELETION;
  }
}

