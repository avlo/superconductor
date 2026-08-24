package com.prosilion.superconductor.lib.redis.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.redis.entity.DeletionEventNosqlEntityIF;
import com.prosilion.superconductor.lib.redis.entity.EventNosqlEntityIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
//    log.debug("(1of3save) save(EventIF event):\n  {}", event.createPrettyPrintJson());
//    EventNosqlEntityIF save = eventNosqlEntityService.save(event);
//    log.debug("... (2of3save) returned EventNosqlEntityIF toString()...:\n  {}", save);
//    GenericEventRecord genericEventRecord = save.asGenericEventRecord();
//    log.debug("... (3of3save) returned EventNosqlEntityIF asGenericEventRecord()...:\n  {}", genericEventRecord);
    return eventNosqlEntityService.save(event).asGenericEventRecord();
  }

  private final Function<List<EventNosqlEntityIF>, List<GenericEventRecord>> filteredGERs =
     eventNosqlEntityIFS ->
        asGenericEvents(
           filterDeletionEvents(eventNosqlEntityIFS));
  
  /*
  private final Function<List<EventNosqlEntityIF>, List<GenericEventRecord>> filteredGERs =
     events ->
        events.stream()
           .map(Optional::ofNullable)
           .map(filteredGER)
           .flatMap(Optional::stream)
           .toList();
   */

  private final Function<Optional<EventNosqlEntityIF>, Optional<GenericEventRecord>> filteredGER =
     eventNosqlEntityIF ->
        filterDeletionEvent(eventNosqlEntityIF)
           .map(EventIF::asGenericEventRecord);

  @Override
  public Optional<GenericEventRecord> getEventByEventId(@NonNull String eventId) {
    return eventNosqlEntityService.findByEventIdString(eventId)
       .filter(filterDeletionEvents())
       .map(EventIF::asGenericEventRecord);
  }

  @Override
  public List<GenericEventRecord> getByKind(@NonNull Kind kind) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKind(kind));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKindAndAuthorPublicKey(kind, authorPublicKey));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTag(
     @NonNull Kind kind,
     @NonNull PubKeyTag publicKey) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKindAndPubKeyTag(kind, publicKey));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndEventTag(
     @NonNull Kind kind,
     @NonNull EventTag eventTag) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKindAndEventTag(kind, eventTag));
  }

  @Override
  public Optional<GenericEventRecord> getFirstEventByKindAndEventTag(
     @NonNull Kind kind,
     @NonNull EventTag eventTag) {
    return filteredGER.apply(
       eventNosqlEntityService.getFirstEventByKindAndEventTag(kind, eventTag));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndEventTag(
     @NonNull Kind kind,
     @NonNull PubKeyTag referencePubKeyTag,
     @NonNull EventTag eventTag) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKindAndPubKeyTagAndEventTag(kind, referencePubKeyTag, eventTag));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndIdentifierTag(
     @NonNull Kind kind,
     @NonNull IdentifierTag identifierTag) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKindAndIdentifierTag(kind, identifierTag));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAddressTag(
     @NonNull Kind kind,
     @NonNull AddressTag addressTag) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKindAndAddressTag(kind, addressTag)
          .stream().toList());
  }

  @Override
  public Optional<GenericEventRecord> getFirstEventByKindAndAddressTag(
     @NonNull Kind kind,
     @NonNull AddressTag addressTag) {
    return filteredGER.apply(
       eventNosqlEntityService.getFirstEventByKindAndAddressTag(kind, addressTag));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(
     @NonNull Kind kind,
     @NonNull PubKeyTag referencePubKeyTag,
     @NonNull AddressTag addressTag) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKindAndPubKeyTagAndAddressTag(
          kind,
          referencePubKeyTag,
          addressTag));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(
     @NonNull Kind kind,
     @NonNull PubKeyTag referencePubKeyTag,
     @NonNull IdentifierTag identifierTag) {
    return filteredGERs.apply(
       eventNosqlEntityService.getEventsByKindAndPubKeyTagAndIdentifierTag(
          kind,
          referencePubKeyTag,
          identifierTag));
  }

  @Override
  public Optional<GenericEventRecord> getEventByKindAndAuthorPublicKeyAndIdentifierTag(
     @NonNull Kind kind,
     @NonNull PublicKey authorPublicKey,
     @NonNull IdentifierTag identifierTag) {
    return filteredGERs.apply(
          eventNosqlEntityService.getEventByKindAndAuthorPublicKeyAndIdentifierTag(
             kind,
             authorPublicKey,
             identifierTag).stream().toList())
       .stream().findFirst();
  }

  @Override
  public List<GenericEventRecord> getAll() {
    return filteredGERs.apply(eventNosqlEntityService.getAll());
  }

  @Override
  public List<GenericEventRecord> getAllIncludingDeleted() {
    return asGenericEvents(eventNosqlEntityService.getAll().stream());
  }

  @NonNull
  private List<GenericEventRecord> asGenericEvents(Stream<EventNosqlEntityIF> events) {
    return events.map(EventIF::asGenericEventRecord).toList();
  }

  private Stream<EventNosqlEntityIF> filterDeletionEvents(List<EventNosqlEntityIF> events) {
    return events.stream().filter(filterDeletionEvents());
  }

  private Optional<EventNosqlEntityIF> filterDeletionEvent(Optional<EventNosqlEntityIF> event) {
    return event.filter(filterDeletionEvents());
  }

  private Predicate<EventNosqlEntityIF> filterDeletionEvents() {
    return eventNosqlEntityIF ->
       !getAllDeletionEventIds().contains(eventNosqlEntityIF.getEventId());
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    deleteEventTags(eventIF, deletionEventNoSqlEntityService::addDeletionEvent);
  }

  @Override
  public List<String> getAllDeletionEventIds() {
    return deletionEventNoSqlEntityService.getAll().stream().map(DeletionEventNosqlEntityIF::getEventId).toList();
  }

  private void deleteEventTags(
     @NonNull EventIF event,
     @NonNull Consumer<EventNosqlEntityIF> addDeletionEvent) {
    event.getTypeSpecificTags(EventTag.class).stream()
       .map(EventTag::getEventId)
       .map(eventNosqlEntityService::findByEventIdString)
       .flatMap(Optional::stream)
       .filter(deletionCandidate ->
          deletionCandidate.getPublicKey().equals(event.getPublicKey()))
       .forEach(addDeletionEvent);
  }
}
