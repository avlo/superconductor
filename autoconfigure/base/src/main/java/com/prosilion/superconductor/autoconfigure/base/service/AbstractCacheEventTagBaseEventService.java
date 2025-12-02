package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.user.PublicKey;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;

@Slf4j
public abstract class AbstractCacheEventTagBaseEventService implements CacheEventTagBaseEventServiceIF {
  private final CacheServiceIF cacheServiceIF;

  public AbstractCacheEventTagBaseEventService(CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void save(EventIF event) {
    log.info("{} saving event: [{}] ...", getClass().getSimpleName(), event.toString());
    log.info("... with eventId [{}] ...", event.getId());
    List<EventTag> eventTags = Filterable.getTypeSpecificTags(EventTag.class, event);
    if (!eventTags.isEmpty()) {
      log.info("... with EventTag(s):");
    }
    eventTags.forEach(tag -> log.info("eventId [{}], event URL [{}]", tag.getIdEvent(), tag.getRecommendedRelayUrl()));
    List<GenericEventRecord> foundDbEvents = eventTags.stream()
        .map(EventTag::getIdEvent)
        .map(cacheServiceIF::getEventByEventId)
        .flatMap(Optional::stream)
        .toList();

    if (!foundDbEvents.isEmpty()) {
      log.info("... found related DB events:");
    }
    foundDbEvents.forEach(ger -> log.info("eventId [{}]", ger.getId()));

    List<String> missingEventIds = eventTags.stream()
        .map(EventTag::getIdEvent).filter(eventTag ->
            !foundDbEvents.stream().map(GenericEventRecord::getId).toList().contains(eventTag)).toList();

    if (!missingEventIds.isEmpty()) {
      log.info("... missing required DB events:");
    }
    missingEventIds.forEach(eventId -> log.info("eventId [{}]", eventId));

    if (!missingEventIds.isEmpty())
      throw new NostrException(
          Strings.concat(
              String.format(CacheFormulaEventService.NON_EXISTENT_EVENT_ID_S, event.getId()),
              Strings.join(missingEventIds.stream().map(eventId -> String.format("[%s]", eventId)).toList(), ',')));

    log.info("all required EventTag events found, saving event with eventId [{}] ...", event.getId());
    cacheServiceIF.save(event);
    log.info("...done");
  }

  @Override
  public <U extends BaseEvent> U createEventGivenMappedEventTagEvents(
      GenericEventRecord eventToPopulate,
      Class<U> eventClassFromKind,
      Function<EventTag, ? extends BaseEvent> fxn) {
    U eventWithEventTagsMappedEvents = createBaseEvent(
        eventToPopulate,
        eventClassFromKind,
        fxn);
    return eventWithEventTagsMappedEvents;
  }

  public <T extends BaseEvent> T createBaseEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<T> baseEventFromKind) {
    T event = cacheServiceIF.createBaseEvent(
        genericEventRecord,
        baseEventFromKind);
    return event;
  }

  abstract public EventTagsMappedEventsIF populate(GenericEventRecord baseEvent, List<GenericEventRecord> unpopulatedEvents);

  @Override
  public Optional<? extends EventTagsMappedEventsIF> getEventByEventId(@NonNull String eventId) {
    Optional<GenericEventRecord> optionalFoundRecord = cacheServiceIF.getEventByEventId(eventId);
    if (optionalFoundRecord.isPresent()) {
      return optionalFoundRecord.map(genericEventRecord ->
          populate(genericEventRecord, optionalFoundRecord.map(this::getEventTagsAsGenericEventRecords).orElseThrow()));
    }
    return Optional.empty();
  }

  @Override
  public List<? extends EventTagsMappedEventsIF> getByKind(@NonNull Kind kind) {
    List<GenericEventRecord> eventsByKind = cacheServiceIF.getByKind(kind);
    return eventsByKind.stream().map(eventTag ->
        populate(eventTag, getEventTagsAsGenericEventRecords(eventTag))).toList();
  }

  @Override
  public List<? extends EventTagsMappedEventsIF> getEventsByKindAndAuthorPublicKey(@NonNull Kind kind, @NonNull PublicKey authorPublicKey) {
    List<GenericEventRecord> eventsByKindAndAuthorPublicKey = cacheServiceIF.getEventsByKindAndAuthorPublicKey(kind, authorPublicKey);
    List<EventTagsMappedEventsIF> list = eventsByKindAndAuthorPublicKey.stream().map(eventTag ->
        populate(eventTag, getEventTagsAsGenericEventRecords(eventTag))).toList();
    return list;
  }

  @Override
  public List<? extends EventTagsMappedEventsIF> getEventsByKindAndPubKeyTag(@NonNull Kind kind, @NonNull PublicKey referencePubKeyTag) {
    List<GenericEventRecord> eventsByKindAndPubKeyTag = cacheServiceIF.getEventsByKindAndPubKeyTag(kind, referencePubKeyTag);
    List<EventTagsMappedEventsIF> list = eventsByKindAndPubKeyTag.stream().map(eventTag ->
        populate(eventTag, getEventTagsAsGenericEventRecords(eventTag))).toList();
    return list;
  }

  @Override
  public List<? extends EventTagsMappedEventsIF> getEventsByKindAndPubKeyTagAndAddressTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencePubKeyTag,
      @NonNull AddressTag addressTag) {
    List<GenericEventRecord> eventsByKindAndPubKeyTagAndAddressTag = cacheServiceIF.getEventsByKindAndPubKeyTagAndAddressTag(kind, referencePubKeyTag, addressTag);
    List<EventTagsMappedEventsIF> list = eventsByKindAndPubKeyTagAndAddressTag.stream().map(eventTag ->
        populate(eventTag, getEventTagsAsGenericEventRecords(eventTag))).toList();
    return list;
  }

  @Override
  public List<? extends EventTagsMappedEventsIF> getEventsByKindAndPubKeyTagAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey referencedPubkeyTag,
      @NonNull IdentifierTag identifierTag) {
    List<GenericEventRecord> eventsByKindAndPubKeyTagAndIdentifierTag = cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, referencedPubkeyTag, identifierTag);
    List<EventTagsMappedEventsIF> list = eventsByKindAndPubKeyTagAndIdentifierTag.stream().map(eventTag ->
        populate(eventTag, getEventTagsAsGenericEventRecords(eventTag))).toList();
    return list;
  }

  @Override
  public List<? extends EventTagsMappedEventsIF> getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
      @NonNull Kind kind,
      @NonNull PublicKey authorPublicKey,
      @NonNull IdentifierTag identifierTag) {
    List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(kind, authorPublicKey, identifierTag);
    List<EventTagsMappedEventsIF> list = eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().map(eventTag ->
        populate(eventTag, getEventTagsAsGenericEventRecords(eventTag))).toList();
    return list;
  }

  public List<GenericEventRecord> getEventTagsAsGenericEventRecords(@NonNull GenericEventRecord genericEventRecord) {
    List<GenericEventRecord> eventTagsAsGenericEventRecords = genericEventRecord.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(eventTag ->
            cacheServiceIF.getEventByEventId(eventTag.getIdEvent()))
        .flatMap(Optional::stream).toList();
    return eventTagsAsGenericEventRecords;
  }

  @Override
  public Optional<? extends EventTagsMappedEventsIF> getEvent(@NonNull EventIF eventIF) {
    return getEventByEventId(eventIF.getId());
  }

  @Override
  public List<EventTagsMappedEventsIF> getAll() {
    List<GenericEventRecord> all = cacheServiceIF.getAll();
    return all.stream().map(eventTag -> populate(eventTag, all)).toList();
  }

  @Override
  public void deleteEvent(@NonNull EventIF eventIF) {
    cacheServiceIF.deleteEvent(eventIF);
  }
}
