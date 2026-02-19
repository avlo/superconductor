package com.prosilion.superconductor.lib.jpa.service;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.lib.jpa.entity.EventJpaEntityIF;
import com.prosilion.superconductor.lib.jpa.entity.join.deletion.DeletionEventJpaEntityIF;
import java.util.List;
import java.util.Optional;
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
    return eventJpaEntityService.getEventByUid(
            eventJpaEntityService.save(event))
        .orElseThrow()
        .asGenericEventRecord();
  }

  @Override
  public Optional<GenericEventRecord> getEventByEventId(@NonNull String eventId) {
    return eventJpaEntityService.findByEventIdString(eventId).stream()
        .filter(filterDeletionEvents())
        .findFirst()
        .map(EventIF::asGenericEventRecord);
  }

  @Override
  public Optional<GenericEventRecord> getJpaEventByUid(Long id) {
    return eventJpaEntityService.getEventByUid(id).stream()
        .filter(filterDeletionEvents())
        .findFirst()
        .map(EventIF::asGenericEventRecord);
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull EventIF eventIF) {
    return eventJpaEntityService.findByEventIdString(eventIF.getId()).stream()
        .filter(filterDeletionEvents())
        .findFirst()
        .map(EventIF::asGenericEventRecord);
  }

  @Override
  public List<GenericEventRecord> getByKind(@NonNull Kind kind) {
    return eventJpaEntityService.getEventsByKind(kind).stream()
        .filter(filterDeletionEvents())
        .map(EventIF::asGenericEventRecord).toList();
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    return eventJpaEntityService.getEventsByKindAndAuthorPublicKey(kind, authorPublicKey).stream()
        .filter(filterDeletionEvents())
        .map(EventIF::asGenericEventRecord).toList();
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTag(
      @NonNull Kind kind,
      @NonNull PublicKey publicKey) {
    return eventJpaEntityService.getEventsByKind(kind).stream()
        .filter(filterDeletionEvents())
        .map(EventIF::asGenericEventRecord).toList();
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndAddressTag(Kind kind, PublicKey referencePubKeyTag, AddressTag addressTag) {
    return eventJpaEntityService.getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag).stream()
        .filter(filterDeletionEvents())
        .map(EventIF::asGenericEventRecord).toList();
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndPubKeyTagAndIdentifierTag(Kind kind, PublicKey referencedPubkeyTag, IdentifierTag identifierTag) {
    return eventJpaEntityService.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, referencedPubkeyTag, identifierTag).stream()
        .filter(filterDeletionEvents())
        .map(EventIF::asGenericEventRecord).toList();
  }

  @Override
  public List<GenericEventRecord> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(Kind kind, PublicKey authorPublicKey, IdentifierTag identifierTag) {
    return eventJpaEntityService.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(kind, authorPublicKey, identifierTag).stream()
        .filter(filterDeletionEvents())
        .map(EventIF::asGenericEventRecord).toList();
  }

  @Override
  public List<GenericEventRecord> getAll() {
    return eventJpaEntityService.getAll().stream()
        .filter(filterDeletionEvents())
        .map(EventIF::asGenericEventRecord).toList();
  }

  private Predicate<EventJpaEntityIF> filterDeletionEvents() {
    return eventJpaEntityIF ->
        !getAllDeletionEventIds().contains(eventJpaEntityIF.getUid());
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    eventIF.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(EventTag::getIdEvent)
        .map(eventJpaEntityService::findByEventIdString)
        .flatMap(Optional::stream)
        .filter(deletionCandidate ->
            deletionCandidate.getPublicKey().equals(eventIF.getPublicKey()))
        .forEach(deletionEventJpaEntityService::addDeletionEvent);
  }

  @Override
  public List<Long> getAllDeletionEventIds() {
    return deletionEventJpaEntityService.getAll().stream()
        .map(DeletionEventJpaEntityIF::getId).toList();
  }

}
