package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.autoconfigure.base.config.NostrRelayReqConsolidatorService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceEventTagService extends CacheDereferenceAbstractTagService<EventTag> implements CacheDereferenceEventTagServiceIF {
  public static final String INVALID_REMOTE_URL = "AbstractEventTag [%s] does not contain a (valid) Relay";

  public CacheDereferenceEventTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl,
      @NonNull NostrRelayReqConsolidatorService nostrRelayReqConsolidatorService) {
    super(cacheServiceIF, superconductorRelayUrl, nostrRelayReqConsolidatorService);
  }

  @Override
  public List<GenericEventRecord> getEvents(List<EventTag> eventTags) {
    return eventTags
        .parallelStream()
        .map(this::getEvent)
        .flatMap(Optional::stream).toList();
  }

  @Override
  Optional<GenericEventRecord> getEventFxn(EventTag eventTag) {
    log.debug("getEvent(EventTag), id: [{}], eventTag URL: [{}]",
        eventTag.getIdEvent(),
        eventTag.getRecommendedRelayUrl());
    Optional<GenericEventRecord> cacheServiceIFEventByEventId = cacheServiceIF.getEventByEventId(eventTag.getIdEvent());

    boolean present = cacheServiceIFEventByEventId.isPresent();
    if (present) {
      log.debug("... returning local EventTag, id: [{}], eventTag URL: [{}]\n",
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
