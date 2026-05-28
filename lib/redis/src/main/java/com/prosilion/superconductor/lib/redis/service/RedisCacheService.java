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

  private final Function<List<EventNosqlEntityIF>, List<GenericEventRecord>> filteredGER = eventNosqlEntityIFS ->
      asGenericEvents(
          filterDeletionEvents(eventNosqlEntityIFS));

  @Override
  public Optional<GenericEventRecord> getEventByEventId(@NonNull String eventId) {
    return filteredGER.apply(
            eventNosqlEntityService.findByEventIdString(eventId).stream().toList())
        .stream().findFirst();
  }

  @Override
  public List<GenericEventRecord> getByKind(@NonNull Kind kind) {
    return filteredGER.apply(
        eventNosqlEntityService.getEventsByKind(kind));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    return filteredGER.apply(
        eventNosqlEntityService.getEventsByKindAndAuthorPublicKey(kind, authorPublicKey));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PubKeyTag publicKey) {
    return filteredGER.apply(
        eventNosqlEntityService.getEventsByKindAndPubKeyTag(kind, publicKey));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull IdentifierTag identifierTag) {
    return filteredGER.apply(
        eventNosqlEntityService.getEventsByKindAndIdentifierTag(kind, identifierTag));
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAddressTag(
      @NonNull Kind kind,
      @NonNull AddressTag addressTag) {
    return filteredGER.apply(
        eventNosqlEntityService.getEventsByKindAndAddressTag(kind, addressTag)
            .stream().toList());
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(
      @NonNull Kind kind,
      @NonNull PubKeyTag referencePubKeyTag,
      @NonNull AddressTag addressTag) {
    return filteredGER.apply(
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
    return filteredGER.apply(
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

    Optional<GenericEventRecord> apply =
        filteredGER.apply(
                eventNosqlEntityService.getEventByKindAndAuthorPublicKeyAndIdentifierTag(
                    kind,
                    authorPublicKey,
                    identifierTag).stream().toList())
            .stream().findFirst();
    return apply;
  }

  @Override
  public List<GenericEventRecord> getAll() {
    return filteredGER.apply(eventNosqlEntityService.getAll());
  }

  @NonNull
  private List<GenericEventRecord> asGenericEvents(Stream<EventNosqlEntityIF> events) {
    return events.map(EventIF::asGenericEventRecord).toList();
  }

  private Stream<EventNosqlEntityIF> filterDeletionEvents(List<EventNosqlEntityIF> events) {
    return events.stream().filter(filterDeletionEvents());
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
