package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAbstractTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.RemoteEventQueryServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class CacheReferenceAbstractTagService<T extends ReferencedAbstractEventTag> implements CacheReferenceAbstractTagServiceIF<T> {
  protected final CacheServiceIF cacheServiceIF;
  private final RemoteEventQueryServiceIF remoteEventQueryServiceIF;

  public CacheReferenceAbstractTagService(@NonNull CacheServiceIF cacheServiceIF, @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.remoteEventQueryServiceIF = remoteEventQueryServiceIF;
  }

  protected abstract Optional<GenericEventRecord> tryGetLocalExpandedEvent(@NonNull T tag);

  @Override
  public Optional<GenericEventRecord> getByExpanded(@NonNull T abstractTag, Kind... kind) {
    Optional<GenericEventRecord> genericEventRecord = tryGetLocalExpandedEvent(abstractTag);
    return genericEventRecord.or(() -> abstractTag.findRelay().flatMap(relay -> {
      Optional<GenericEventRecord> gerOptional = remoteEventQueryServiceIF.sendRemoteReq(relay.getUrl(), createFilters(abstractTag, kind)).stream().findFirst();
      log.info("remoteEventQueryServiceIF.sendRemoteReq(...) returned:\n  {}", gerOptional.map(ger -> ger.createPrettyPrintJson()).orElse("[EMPTY OPTIONAL]"));
      gerOptional.ifPresent(cacheServiceIF::save);
      return gerOptional;
    }));
  }

  protected abstract Filters createFilters(@NonNull T tag, Kind... kind);
}
