package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import com.prosilion.superconductor.base.util.NostrRelayReqConsolidatorService;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceEventTagService implements CacheDereferenceEventTagServiceIF {
  public static final String INVALID_REMOTE_URL = "EventTag [%s] eventID [%s] was not found locally yet also had an invalid remote url [%s]";
  private final CacheServiceIF cacheServiceIF;
  @Getter
  private final String superconductorRelayUrl;
  @Getter
  private final NostrRelayReqConsolidatorService nostrRelayReqConsolidatorService;

  public CacheDereferenceEventTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl) {
    log.debug("Ctor() loaded CacheDereferenceEventTag relay URL: {}", superconductorRelayUrl);
    this.cacheServiceIF = cacheServiceIF;
    this.superconductorRelayUrl = superconductorRelayUrl;
    this.nostrRelayReqConsolidatorService = new NostrRelayReqConsolidatorService();
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull EventTag eventTag, Duration timeout) throws JsonProcessingException {
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

    log.debug("local EventTag not found, calling remoteEventSupplier...");
    return getGenericEventRecord(eventTag, timeout);
  }

  private Optional<GenericEventRecord> getGenericEventRecord(EventTag eventTag, Duration timeout) throws JsonProcessingException {
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
            eventTagFiltersFunction,
            timeout);

    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event {} saved to local DB", genericEventRecord));
    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);

    return optionalGenericEventRecord;
  }
}
