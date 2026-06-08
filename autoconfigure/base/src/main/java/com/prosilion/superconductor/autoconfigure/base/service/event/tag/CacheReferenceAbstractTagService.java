package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAbstractTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import lombok.NonNull;

@Slf4j
public abstract class CacheReferenceAbstractTagService<T extends ReferencedAbstractEventTag> implements CacheReferenceAbstractTagServiceIF<T> {
  private static final String STRING = "inside getRemoteEventGenericEventRecord(abstractTag, relayUrl):\n  [{}],\n  [{}]";
  protected final CacheServiceIF cacheServiceIF;
  private final RemoteAbstractTagService remoteAbstractTagService;

  public CacheReferenceAbstractTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull RemoteAbstractTagService remoteAbstractTagService) {
    this.cacheServiceIF = cacheServiceIF;
    this.remoteAbstractTagService = remoteAbstractTagService;
  }

  abstract Filters getAbstractTagFilters(@NonNull T tag);
  abstract Optional<GenericEventRecord> getLocalEventFxn(@NonNull T tag);

  @Override
  public Optional<GenericEventRecord> getBy(@NonNull T abstractTag) {
    log.debug("inside getEvent(T abstractTag) with abstractTag:\n{}", Util.prettyPrintReferencedAbstractEventTag(abstractTag));

    log.debug("... calling getLocalEventFxn(abstractTag) ...");
    Optional<GenericEventRecord> localGenericEventRecordOptional = getLocalEventFxn(abstractTag);

    if (localGenericEventRecordOptional.isPresent()) {
      log.debug("... returning local GenericEventRecord:{}", localGenericEventRecordOptional.get().createPrettyPrintJson());
      return localGenericEventRecordOptional;
    }

    log.debug("local AbstractTag not found, calling getRemoteEventGenericEventRecord ...");
    return getRemoteEventGenericEventRecord(abstractTag, abstractTag.requireRelay().getUrl());
  }

  protected Optional<GenericEventRecord> getRemoteEventGenericEventRecord(T abstractTag, String relayUrl) {
    log.debug(STRING, Util.prettyPrintReferencedAbstractEventTag(abstractTag), relayUrl);

    Optional<GenericEventRecord> optionalGenericEventRecord = remoteAbstractTagService.sendRemoteReq(
        relayUrl,
        getAbstractTagFilters(abstractTag)).stream().findFirst();

    optionalGenericEventRecord.ifPresentOrElse(genericEventRecord ->
            log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()),
        () -> {
          log.debug("sendConsolidatorReq() did not find an event, throw NostrException");
          throw new NostrException("sendConsolidatorReq() did not find an event");
        });

    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);
    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()));

    return optionalGenericEventRecord;
  }
}
