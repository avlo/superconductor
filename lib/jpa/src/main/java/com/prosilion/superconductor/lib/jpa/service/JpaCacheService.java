package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class JpaCacheService implements JpaCacheServiceIF {
  private final EventJpaEntityService eventJpaEntityService;
  private final DeletionEventJpaEntityService deletionEventJpaEntityService;

  public JpaCacheService(
      @NonNull EventJpaEntityService eventJpaEntityService,
      @NonNull DeletionEventJpaEntityService deletionEventJpaEntityService) {
    this.eventJpaEntityService = eventJpaEntityService;
    this.deletionEventJpaEntityService = deletionEventJpaEntityService;
  }

  @Override
  public GenericEventRecord save(@NonNull EventIF event) {
    Long eventNosqlEntityIF = eventJpaEntityService.save(event);
    EventJpaEntityIF eventByUid = eventJpaEntityService.getEventByUid(eventNosqlEntityIF).orElseThrow();
    GenericEventRecord genericEventRecord = CacheServiceIF.createGenericEventRecordFromEntityIF(eventByUid);
    return genericEventRecord;
  }

  @Override
  public Optional<GenericEventRecord> getEventByEventId(@NonNull String eventId) {
    Optional<EventJpaEntityIF> byEventIdString = eventJpaEntityService.findByEventIdString(eventId).stream().filter(filterDeletionEvents()).findFirst();
    Optional<GenericEventRecord> t = byEventIdString.map(CacheServiceIF::createGenericEventRecordFromEntityIF);
    return t;
  }

  @Override
  public Optional<GenericEventRecord> getJpaEventByUid(Long id) {
    Optional<EventJpaEntityIF> eventByUid = eventJpaEntityService.getEventByUid(id).stream().filter(filterDeletionEvents()).findFirst();
    Optional<GenericEventRecord> first = eventByUid.map(CacheServiceIF::createGenericEventRecordFromEntityIF);
    return first;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull EventIF eventIF) {
    Optional<EventJpaEntityIF> byEventIdString = eventJpaEntityService.findByEventIdString(eventIF.getId()).stream().filter(filterDeletionEvents()).findFirst();
    Optional<GenericEventRecord> first = byEventIdString.map(CacheServiceIF::createGenericEventRecordFromEntityIF);
    return first;
  }

  @Override
  public List<GenericEventRecord> getByKind(@NonNull Kind kind) {
    List<EventJpaEntityIF> eventsByKind = eventJpaEntityService.getEventsByKind(kind).stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> collect = eventsByKind.stream().map(CacheServiceIF::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    List<EventJpaEntityIF> eventsByKind = eventJpaEntityService.getEventsByKindAndAuthorPublicKey(kind, authorPublicKey).stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> collect = eventsByKind.stream().map(CacheServiceIF::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    List<EventJpaEntityIF> eventsByKind = eventJpaEntityService.getEventsByKind(kind).stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> collect = eventsByKind.stream().map(CacheServiceIF::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PublicKey referencePubKeyTag, AddressTag addressTag) {
    List<EventJpaEntityIF> eventsByKindAndPubKeyTagAndAddressTag = eventJpaEntityService.getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag).stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> collect = eventsByKindAndPubKeyTagAndAddressTag.stream().map(CacheServiceIF::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag) {
    List<EventJpaEntityIF> eventsByKindAndPubKeyTagAndAddressTag = eventJpaEntityService.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, referencedPubkeyTag, identifierTag).stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> collect = eventsByKindAndPubKeyTagAndAddressTag.stream().map(CacheServiceIF::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag) {
    List<EventJpaEntityIF> eventsByKindAndPubKeyTagAndAddressTag = eventJpaEntityService.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(kind, authorPublicKey, identifierTag).stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> collect = eventsByKindAndPubKeyTagAndAddressTag.stream().map(CacheServiceIF::createGenericEventRecordFromEntityIF).toList();
    return collect;
  }

  @Override
  public List<GenericEventRecord> getAll() {
    List<EventJpaEntityIF> list = eventJpaEntityService.getAll().stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> genericEventRecords = list.stream().map(CacheServiceIF::createGenericEventRecordFromEntityIF).toList();
    return genericEventRecords;
  }

  private Predicate<EventJpaEntityIF> filterDeletionEvents() {
    return eventJpaEntityIF ->
        !getAllDeletionEventIds().stream()
            .toList()
            .contains(eventJpaEntityIF.getUid());
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    deleteEventTags(eventIF, deletionEventJpaEntityService::addDeletionEvent);
  }

  @Override
  public List<Long> getAllDeletionEventIds() {
    List<DeletionEventJpaEntityIF> all = deletionEventJpaEntityService.getAll();
    return all.stream().map(DeletionEventJpaEntityIF::getId).toList();
  }

  void deleteEventTags(
      @NonNull EventIF eventIF,
      @NonNull Consumer<EventJpaEntityIF> addDeletionEvent) {
    eventIF.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent)
        .map(eventJpaEntityService::findByEventIdString)
        .flatMap(Optional::stream)
        .filter(deletionCandidate ->
            deletionCandidate.getPublicKey().equals(eventIF.getPublicKey()))
        .forEach(addDeletionEvent);
  }
}
