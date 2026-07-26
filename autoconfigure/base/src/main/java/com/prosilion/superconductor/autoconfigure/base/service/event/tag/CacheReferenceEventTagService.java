package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.RemoteEventQueryServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public class CacheReferenceEventTagService extends CacheReferenceAbstractTagService<EventTag> implements CacheReferenceEventTagServiceIF {
  public CacheReferenceEventTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    super(cacheServiceIF, remoteEventQueryServiceIF);
  }

  @Override
  public List<GenericEventRecord> getExpandedEvents(@NonNull List<EventTag> eventTags) {
    return
       eventTags.stream().<GenericEventRecord>mapMulti(
          (eventTag, genericEventRecordConsumer) ->
             tryGetLocalExpandedEvent(eventTag).ifPresent(genericEventRecordConsumer)).toList();
  }

  @Override
  protected Optional<GenericEventRecord> tryGetLocalExpandedEvent(@NonNull EventTag eventTag) {
    return cacheServiceIF.getEventByEventId(eventTag.getEventId());
  }

  @Override
  protected Filters createFilters(@NonNull EventTag eventTag) {
    return TagFilterFactory.forEvent(eventTag);
  }
}
