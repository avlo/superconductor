package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceEventTagService extends CacheDereferenceAbstractTagService<EventTag> implements CacheDereferenceEventTagServiceIF {
  public CacheDereferenceEventTagService(@NonNull CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }

  @Override
  public List<GenericEventRecord> getEvents(List<EventTag> eventTags) {
    log.debug("getEvents(List<EventTag> eventTags), calling streamed getEvent()...");
    List<GenericEventRecord> genericEventRecords = eventTags
        .stream()
        .map(this::getEvent)
        .flatMap(Optional::stream).toList();
    log.debug("streamed getEvent() returning:\n {}", Util.prettyPrintGenericEventRecords(genericEventRecords));
    return genericEventRecords;
  }

  @Override
  Optional<GenericEventRecord> getLocalEventFxn(EventTag eventTag) {
    log.debug("getLocalEventFxn(EventTag), id: [{}], eventTag URL: [{}]",
        eventTag.getIdEvent(),
        eventTag.getRecommendedRelayUrl());
    Optional<GenericEventRecord> cacheServiceIFEventByEventId = cacheServiceIF.getEventByEventId(eventTag.getIdEvent());

    boolean present = cacheServiceIFEventByEventId.isPresent();
    if (present) {
      log.debug("... returning local EventTag, id: [{}], eventTag URL: [{}]",
          eventTag.getIdEvent(),
          eventTag.getRecommendedRelayUrl());
      return cacheServiceIFEventByEventId;
    }

    return Optional.empty();
  }

  @Override
  Filters getAbstractTagFilters(EventTag eventTag) {
    return new Filters(
        new EventFilter(
            new GenericEventId(eventTag.getIdEvent())));
  }
}
