package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAbstractTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.RemoteEventQueryServiceIF;
import java.util.Optional;
import lombok.NonNull;

public abstract class CacheReferenceAbstractTagService<T extends ReferencedAbstractEventTag> implements CacheReferenceAbstractTagServiceIF<T> {
  protected final CacheServiceIF cacheServiceIF;
  private final CacheAsideEventLookup cacheAsideEventLookup;

  public CacheReferenceAbstractTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheAsideEventLookup = new CacheAsideEventLookup(remoteEventQueryServiceIF);
  }

  protected abstract Optional<GenericEventRecord> tryGetLocalExpandedEvent(@NonNull T tag);

  @Override
  public Optional<GenericEventRecord> getByExpanded(@NonNull T abstractTag) {
    return abstractTag.findRelay().flatMap(relay ->
       cacheAsideEventLookup.findFirst(
          () -> tryGetLocalExpandedEvent(abstractTag),
          relay.getUrl(),
          createFilters(abstractTag)));
  }

  protected abstract Filters createFilters(@NonNull T tag);
}
