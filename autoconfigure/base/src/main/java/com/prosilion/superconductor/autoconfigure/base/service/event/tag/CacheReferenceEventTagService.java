package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheReferenceEventTagService extends CacheReferenceAbstractTagService<EventTag> implements CacheReferenceEventTagServiceIF {
  public CacheReferenceEventTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteAbstractTagService remoteAbstractTagService) {
    super(cacheServiceIF, remoteAbstractTagService);
  }

  @Override
  public List<GenericEventRecord> getEvents(@NonNull List<EventTag> eventTags) {
    return
       eventTags.stream().<GenericEventRecord>mapMulti(
          (eventTag, genericEventRecordConsumer) ->
             tryGetLocalExpandedEvent(eventTag).ifPresent(genericEventRecordConsumer)).toList();
  }

  @Override
  Optional<GenericEventRecord> tryGetLocalExpandedEvent(@NonNull EventTag eventTag) {
    return cacheServiceIF.getEventByEventId(eventTag.getEventId());
  }

  @Override
  Filters getAbstractTagFilters(@NonNull EventTag eventTag) {
    return new Filters(
       new EventFilter(
          new GenericEventId(eventTag.getEventId())));
  }
}
