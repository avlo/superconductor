package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
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

  public RedisCacheService(
      @NonNull EventNosqlEntityService eventNosqlEntityService,
      @NonNull DeletionEventNoSqlEntityService deletionEventNoSqlEntityService) {
    this.eventNosqlEntityService = eventNosqlEntityService;
    this.deletionEventNoSqlEntityService = deletionEventNoSqlEntityService;
  }

  @Override
  public GenericEventRecord save(EventIF event) {
    EventNosqlEntityIF eventNosqlEntityIF = eventNosqlEntityService.save(event);
    GenericEventRecord genericEventRecord = createGenericEventRecordFromEntityIF(eventNosqlEntityIF);
    return genericEventRecord;
  }

  @Override
  public Optional<GenericEventRecord> getEventByEventId(@NonNull String eventId) {
    Optional<EventNosqlEntityIF> byEventIdString = eventNosqlEntityService.findByEventIdString(eventId);
    Optional<GenericEventRecord> t = byEventIdString.map(this::createGenericEventRecordFromEntityIF);
    return t;
  }

  //  TODO: below is duplicate of above
  @Override
  public Optional<GenericEventRecord> getRedisEventByUid(String id) {
    return getEventByEventId(id);
  }

  @Override
  public List<GenericEventRecord> getByKind(@NonNull Kind kind) {
    List<EventNosqlEntityIF> eventsByKind = eventNosqlEntityService.getEventsByKind(kind);
    List<GenericEventRecord> collect = eventsByKind.stream().map(this::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTag = eventNosqlEntityService.getEventsByKindAndPubKeyTag(kind, publicKey);
    List<GenericEventRecord> collect = eventsByKindAndPubKeyTag.stream().map(this::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndIdentifierTag(kind, identifierTag);
    List<GenericEventRecord> list = eventsByKindAndIdentifierTag.stream().map(this::createGenericEventRecordFromEntityIF).toList();
    return list;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencePubKeyTag,
      @NonNull AddressTag addressTag) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTagAndAddressTag = eventNosqlEntityService.getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag);
    List<GenericEventRecord> list = eventsByKindAndPubKeyTagAndAddressTag.stream().map(this::createGenericEventRecordFromEntityIF).toList();
    return list;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencePubKeyTag,
      @NonNull IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTagAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, referencePubKeyTag, identifierTag);
    List<GenericEventRecord> list = eventsByKindAndPubKeyTagAndIdentifierTag.stream().map(this::createGenericEventRecordFromEntityIF).toList();
    return list;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndAuthorPublicKeyAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(kind, authorPublicKey, identifierTag);
    List<GenericEventRecord> list = eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().map(this::createGenericEventRecordFromEntityIF).toList();
    return list;
  }

  @Override
  public List<GenericEventRecord> getAll() {
    List<EventNosqlEntityIF> list = eventNosqlEntityService.getAll().stream()
        .filter(eventNosqlEntityIF ->
            !getAllDeletionEventIds().stream()
                .toList()
                .contains(eventNosqlEntityIF.getEventId())).toList();

    List<GenericEventRecord> list1 = list.stream().map(this::createGenericEventRecordFromEntityIF).toList();

    List<GenericEventRecord> list2 = list1.stream().toList();

    return list2;
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    eventNosqlEntityService.findByEventIdString(eventIF.getId()).ifPresent(deletionEventNoSqlEntityService::addDeletionEvent);
//    TODO: revisit below rationale:
//    do not delete eventTags as they are/likely referenced by other events 
//    deleteEventTags(event, deletionEventNoSqlEntityService::addDeletionEvent);
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
