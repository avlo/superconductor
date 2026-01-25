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
import java.util.function.Predicate;
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
    GenericEventRecord saved = eventNosqlEntityIF.asGenericEventRecord();
    return saved;
  }

  @Override
  public Optional<GenericEventRecord> getEventByEventId(@NonNull String eventId) {
    Optional<EventNosqlEntityIF> byEventIdString = eventNosqlEntityService.findByEventIdString(eventId).stream().filter(filterDeletionEvents()).findFirst();
    Optional<GenericEventRecord> match = byEventIdString.map(EventIF::asGenericEventRecord);
    return match;
  }

  @Override
  public List<GenericEventRecord> getByKind(@NonNull Kind kind) {
    List<EventNosqlEntityIF> eventsByKind = eventNosqlEntityService.getEventsByKind(kind)
        .stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> matches = eventsByKind.stream().map(EventIF::asGenericEventRecord).toList();
    return matches;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    List<EventNosqlEntityIF> eventsByKind = eventNosqlEntityService.getEventsByKindAndAuthorPublicKey(kind, authorPublicKey)
        .stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> matches = eventsByKind.stream().map(EventIF::asGenericEventRecord).toList();
    return matches;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTag = eventNosqlEntityService.getEventsByKindAndPubKeyTag(kind, publicKey).stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> matches = eventsByKindAndPubKeyTag.stream().map(EventIF::asGenericEventRecord).toList();
    return matches;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndIdentifierTag(kind, identifierTag).stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> matches = eventsByKindAndIdentifierTag.stream().map(EventIF::asGenericEventRecord).toList();
    return matches;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencePubKeyTag,
      @NonNull AddressTag addressTag) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTagAndAddressTag = eventNosqlEntityService.getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag)
        .stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> matches = eventsByKindAndPubKeyTagAndAddressTag.stream().map(EventIF::asGenericEventRecord).toList();
    return matches;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencePubKeyTag,
      @NonNull IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndPubKeyTagAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, referencePubKeyTag, identifierTag)
        .stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> matches = eventsByKindAndPubKeyTagAndIdentifierTag.stream().map(EventIF::asGenericEventRecord).toList();
    return matches;
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey authorPublicKey,
      @NonNull IdentifierTag identifierTag) {
    List<EventNosqlEntityIF> eventsByKindAndAuthorPublicKeyAndIdentifierTag = eventNosqlEntityService.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(kind, authorPublicKey, identifierTag).stream()
        .filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> matches = eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().map(EventIF::asGenericEventRecord).toList();
    return matches;
  }

  @Override
  public List<GenericEventRecord> getAll() {
    List<EventNosqlEntityIF> list = eventNosqlEntityService.getAll().stream().filter(filterDeletionEvents()).toList();
    List<GenericEventRecord> all = list.stream().map(EventIF::asGenericEventRecord).toList();
    return all;
  }

  private Predicate<EventNosqlEntityIF> filterDeletionEvents() {
    return eventNosqlEntityIF ->
        !getAllDeletionEventIds().stream()
            .toList()
            .contains(eventNosqlEntityIF.getEventId());
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    deleteEventTags(eventIF, deletionEventNoSqlEntityService::addDeletionEvent);
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
