package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceEventTagService implements CacheDereferenceEventTagServiceIF {
  public static final String INVALID_REMOTE_URL = "EventTag [%s] eventID [%s] was not found locally yet also had an invalid remote url [%s]";
  private final CacheServiceIF cacheServiceIF;

  public CacheDereferenceEventTagService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull EventTag eventTag) {
    log.debug("{} getEvent(EventTag) containing eventId: [{}], eventTag URL: [{}]",
        getClass().getSimpleName(),
        eventTag.getIdEvent(),
        eventTag.getRecommendedRelayUrl());
    Optional<GenericEventRecord> cacheServiceIFEventByEventId = cacheServiceIF.getEventByEventId(eventTag.getIdEvent());

    boolean present = cacheServiceIFEventByEventId.isPresent();
    if (present) {
      log.debug("... returning local EventTag containing eventId: [{}], eventTag URL: [{}]",
          eventTag.getIdEvent(),
          eventTag.getRecommendedRelayUrl());
      return cacheServiceIFEventByEventId;
    }

    log.debug("local EventTag not found, calling remoteEventSupplier...");
    return getGenericEventRecord(eventTag);
  }

  private Optional<GenericEventRecord> getGenericEventRecord(EventTag eventTag) {
    String recommendedRelayUrl = Optional.ofNullable(
        eventTag.getRecommendedRelayUrl()).orElseThrow(() ->
        new NostrException(
            String.format(INVALID_REMOTE_URL,
                eventTag,
                eventTag.getIdEvent(),
                eventTag.getRecommendedRelayUrl())));

    Function<EventTag, Filters> eventTagFiltersFunction = fxnEventTag ->
        new Filters(
            new EventFilter(
                new GenericEventId(eventTag.getIdEvent())));

    Optional<GenericEventRecord> optionalGenericEventRecord =
        remoteEventSupplier(
            recommendedRelayUrl,
            eventTag,
            eventTagFiltersFunction);

    return optionalGenericEventRecord;
  }
}
