package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceEventTagService implements CacheDereferenceEventTagServiceIF {
  private final CacheServiceIF cacheServiceIF;

  public CacheDereferenceEventTagService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull EventTag eventTag) {
    Optional<GenericEventRecord> eventByEventTag = cacheServiceIF.getEventByEventId(
        eventTag.getIdEvent());

    Function<EventTag, Filters> filterFxn = fxnEventTag ->
        new Filters(new EventFilter(new GenericEventId(eventTag.getIdEvent())));

    Optional<GenericEventRecord> genericEventRecord =
        eventByEventTag
            .or(
                remoteEventSupplier(
                    Optional.ofNullable(
                        eventTag.getRecommendedRelayUrl()).orElseThrow(),
                    eventTag,
                    filterFxn));
    return genericEventRecord;
  }
}
