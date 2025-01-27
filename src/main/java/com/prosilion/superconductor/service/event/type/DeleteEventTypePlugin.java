package com.prosilion.superconductor.service.event.type;

import com.prosilion.superconductor.entity.EventEntity;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import nostr.event.Kind;
import nostr.event.NIP09Event;
import nostr.event.impl.DeletionEvent;
import nostr.event.impl.GenericTag;
import nostr.event.tag.EventTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Component
public class DeleteEventTypePlugin<T extends NIP09Event> implements EventTypePlugin<T> {
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
    assert (false);
  }

  private void saveEvent(T event) {
    redisCache.saveEventEntity(event); // NIP-09 req's saving of event itself
  }

  private void deleteEvents(T event) {
    List<Optional<EventEntity>> matchingEvents = event.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(eventTag ->
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
            optionalEventEntity.filter(eventEntity ->
                eventEntity.getKind().toString().equals(
                    getStreamStream(kTags).distinct().toString()))).toList();

    eventsToDelete.forEach(optionalEventEntity ->
        optionalEventEntity.ifPresent(redisCache::deleteEventEntity));

// handle "a" tag...
    assert(false);
  }

  private static Stream<String> getStreamStream(List<GenericTag> kTags) {
    Stream<String> stringStream = kTags.stream()
        .map(
            DeleteEventTypePlugin::getDistinct)
        .flatMap(Stream::distinct);

    Stream<String> stringStream1 = Optional.of(stringStream).orElse(Stream.empty());
    return stringStream1;
  }

  private static Stream<String> getDistinct(GenericTag kTag) {
    Stream<String> stringStream = kTag.getAttributes()
        .stream().map(elementAttribute ->
            elementAttribute.getValue().toString())
        .distinct();
    return stringStream;
  }

  @Override
  public Kind getKind() {
    return Kind.DELETION;
  }
}

