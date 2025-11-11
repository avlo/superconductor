package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.KindClassMapService;
import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class RedisCacheService implements RedisCacheServiceIF {
  private final EventNosqlEntityService eventNosqlEntityService;
  private final DeletionEventNoSqlEntityService deletionEventNoSqlEntityService;
  private final KindClassMapService kindClassMapService;

  public RedisCacheService(
      @NonNull EventNosqlEntityService eventNosqlEntityService,
      @NonNull DeletionEventNoSqlEntityService deletionEventNoSqlEntityService,
      @NonNull KindClassMapService kindClassMapService) {
    this.eventNosqlEntityService = eventNosqlEntityService;
    this.deletionEventNoSqlEntityService = deletionEventNoSqlEntityService;
    this.kindClassMapService = kindClassMapService;
  }

  @Override
  public BaseEvent createBaseEvent(GenericEventRecord genericEventRecord) {
    return kindClassMapService.createBaseEvent(genericEventRecord);
  }

  @Override
  public BaseEvent save(EventIF event) {
    if (event instanceof EventTagsMappedEventsIF) {
      EventTagsMappedEventsIF eventTagsMappedEventsIF = (EventTagsMappedEventsIF) event;
    }
    
    EventNosqlEntityIF eventNosqlEntityIF = eventNosqlEntityService.save(event);
    BaseEvent baseEventFromEntityIF = createBaseEventFromEntityIF(eventNosqlEntityIF);
    return baseEventFromEntityIF;
  }

  @Override
  public Optional<? extends BaseEvent> getEventByEventId(@NonNull String eventId) {
    Optional<EventNosqlEntityIF> byEventIdString = eventNosqlEntityService.findByEventIdString(eventId);
    Optional<? extends BaseEvent> t = byEventIdString.map(this::createBaseEventFromEntityIF);
    return t;
  }

  @Override
  public List<? extends BaseEvent> getByKind(@NonNull Kind kind) {
    List<EventNosqlEntityIF> eventsByKind = eventNosqlEntityService.getEventsByKind(kind);
    List<? extends BaseEvent> collect = eventsByKind.stream().map(this::createBaseEventFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<? extends BaseEvent> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTag = eventNosqlEntityService.getEventsByKindAndPubKeyTag(kind, publicKey);
    List<? extends BaseEvent> collect = eventsByKindAndPubKeyTag.stream().map(this::createBaseEventFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<? extends BaseEvent> getEventsByKindAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndIdentifierTag(kind, identifierTag);
    List<? extends BaseEvent> list = eventsByKindAndIdentifierTag.stream().map(this::createBaseEventFromEntityIF).toList();
    return list;
  }

  @Override
  public List<? extends BaseEvent> getEventsByKindAndPubKeyTagAndAddressTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencePubKeyTag,
      @NonNull AddressTag addressTag) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTagAndAddressTag = eventNosqlEntityService.getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag);
    List<? extends BaseEvent> list = eventsByKindAndPubKeyTagAndAddressTag.stream().map(this::createBaseEventFromEntityIF).toList();
    return list;
  }

  @Override
  public List<? extends BaseEvent> getEventsByKindAndPubKeyTagAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencePubKeyTag,
      @NonNull IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTagAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, referencePubKeyTag, identifierTag);
    List<? extends BaseEvent> list = eventsByKindAndPubKeyTagAndIdentifierTag.stream().map(this::createBaseEventFromEntityIF).toList();
    return list;
  }

  @Override
  public List<? extends BaseEvent> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndAuthorPublicKeyAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(kind, authorPublicKey, identifierTag);
    List<? extends BaseEvent> list = eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().map(this::createBaseEventFromEntityIF).toList();
    return list;
  }
//  TODO: possible MapStruct candidate replacement
//  @Override
//  public EventNosqlEntityIF saveWithEventTags(
//      @NonNull EventIF event,
//      @NonNull List<T> eventTagEvents) {
//    final List<String> eventTagIds = Filterable.getTypeSpecificTagsStream(EventTag.class, event)
//        .map(EventTag::getIdEvent).toList();
//    final List<String> eventTagEventIds = eventTagEvents.stream().map(EventIF::getId).toList();

//

//    List<String> nonMatchingEventTagIds = eventTagIds.stream()

  /// /        .filter(eventTagEventIds::contains)
//        .filter(eventTagId -> !eventTagEventIds.contains(eventTagId))
//        .toList();
//    assert Objects.equals(0, nonMatchingEventTagIds.size()) :
//        new MissingMatchingEventException(nonMatchingEventTagIds, true);
//
//    List<String> nonMatchingEventTagEventIds = eventTagEventIds.stream()
//        .filter(eventTagEventId -> !eventTagIds.contains(eventTagEventId))
//        .toList();
//    assert Objects.equals(0, nonMatchingEventTagEventIds.size()) :
//        new MissingMatchingEventException(nonMatchingEventTagEventIds, false);
//
//    eventTagEvents.forEach(this::save);
//    return save(event);
//  }
  @Override
  public List<? extends BaseEvent> getAll() {
    List<EventNosqlEntityIF> list = eventNosqlEntityService.getAll().stream()
        .filter(eventNosqlEntityIF ->
            !getAllDeletionEventIds().stream()
//                .map(DeletionEventIF::getId)
//                .map(BaseEvent::getId)
                .toList()
                .contains(eventNosqlEntityIF.getEventId())).toList();

    List<? extends BaseEvent> list1 = list.stream().map(this::createBaseEventFromEntityIF).toList();

    List<? extends BaseEvent> list2 = list1.stream().toList();

    return list2;
  }

  @Override
  public void deleteEvent(@NonNull EventIF event) {
    deleteEventTags(event, deletionEventNoSqlEntityService::addDeletionEvent);
  }

  @Override
  public List<String> getAllDeletionEventIds() {
    List<DeletionEventNosqlEntityIF> all = deletionEventNoSqlEntityService.getAll();
    return all.stream().map(DeletionEventNosqlEntityIF::getEventId).toList();
  }

  private void deleteEventTags(
      @NonNull EventIF event,
      @NonNull Consumer<EventNosqlEntityIF> addDeletionEvent) {
    event.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent)
        .map(eventNosqlEntityService::findByEventIdString)
        .flatMap(Optional::stream)
        .filter(deletionCandidate ->
            deletionCandidate.getPublicKey().equals(event.getPublicKey()))
        .forEach(addDeletionEvent);
  }
}
