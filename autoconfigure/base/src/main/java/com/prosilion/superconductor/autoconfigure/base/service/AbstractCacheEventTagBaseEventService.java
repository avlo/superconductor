package com.prosilion.superconductor.autoconfigure.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.EventTagsMappedEventsIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.CacheEventTagBaseEventServiceIF;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;

public abstract class AbstractCacheEventTagBaseEventService implements CacheEventTagBaseEventServiceIF {
  private final CacheServiceIF cacheServiceIF;

  public AbstractCacheEventTagBaseEventService(CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public void save(EventIF event) {
    cacheServiceIF.save(event);
  }

  public List<GenericEventRecord> getEventTagMappedEvents(@NonNull EventIF event) {
    List<EventTag> eventTags = Filterable.getTypeSpecificTags(EventTag.class, event);
    List<GenericEventRecord> foundDbEvents = eventTags.stream()
        .map(EventTag::getIdEvent)
        .map(cacheServiceIF::getEventByEventId)
        .flatMap(Optional::stream)
        .toList();

    List<String> missingEventIds = eventTags.stream()
        .map(EventTag::getIdEvent).filter(eventTag ->
            !foundDbEvents.stream().map(GenericEventRecord::getId).toList().contains(eventTag)).toList();

    if (!missingEventIds.isEmpty())
      throw new NostrException(
          Strings.concat(
              String.format(CacheFormulaEventService.NON_EXISTENT_EVENT_ID_S, event.getId()),
              Strings.join(missingEventIds.stream().map(eventId -> String.format("[%s]", eventId)).toList(), ',')));
    return foundDbEvents;
  }

  @Override
  public <U extends BaseEvent> U createEventGivenMappedEventTagEvents(
      GenericEventRecord eventToPopulate,
      Class<U> eventClassFromKind,
      Function<EventTag, ? extends BaseEvent> fxn) {
    return createBaseEvent(eventToPopulate, eventClassFromKind, fxn);
  }

  <T extends BaseEvent> T createBaseEvent(
      @NonNull GenericEventRecord genericEventRecord,
      @NonNull Class<T> baseEventFromKind) {
    return cacheServiceIF.createBaseEvent(genericEventRecord, baseEventFromKind);
  }

  abstract EventTagsMappedEventsIF populate(GenericEventRecord baseEvent, List<GenericEventRecord> unpopulatedEvents);

  @Override
  public Optional<? extends EventTagsMappedEventsIF> getEventByEventId(@NonNull String eventId) {
    Optional<GenericEventRecord> optionalFoundRecord = cacheServiceIF.getEventByEventId(eventId);
    if (optionalFoundRecord.isPresent()) {
      return optionalFoundRecord.map(genericEventRecord ->
          populate(genericEventRecord, optionalFoundRecord.map(this::getList).orElseThrow()));
    }
    return Optional.empty();
  }

  protected List<GenericEventRecord> getList(GenericEventRecord genericEventRecord) {
    return genericEventRecord.getTags().stream()
        .filter(EventTag.class::isInstance)
        .map(EventTag.class::cast)
        .map(eventTag ->
            cacheServiceIF.getEventByEventId(eventTag.getIdEvent()))
        .flatMap(Optional::stream).toList();
  }

  @Override
  public Optional<? extends EventTagsMappedEventsIF> getEvent(@NonNull EventIF eventIF) {
    return getEventByEventId(eventIF.getId());
  }

  @Override
  public List<? extends EventTagsMappedEventsIF> getByKind(@NonNull Kind kind) {
    List<GenericEventRecord> eventsByKind = cacheServiceIF.getByKind(kind);
    return eventsByKind.stream().map(eventTag -> 
        populate(eventTag, getList(eventTag))).toList();
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
