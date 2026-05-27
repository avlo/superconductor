package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.ReferencedAbstractEventTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAbstractTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public abstract class CacheDereferenceAbstractTagService<T extends ReferencedAbstractEventTag> extends CacheDereferenceBaseAbstractTagService<T> implements CacheDereferenceAbstractTagServiceIF<T> {
  private static final String STRING = "inside getRemoteEventGenericEventRecord(abstractTag, relayUrl): [{}], [{}]";

  public CacheDereferenceAbstractTagService(@NonNull CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }

  abstract Optional<GenericEventRecord> getLocalEventFxn(T tag);

  protected Optional<GenericEventRecord> getRemoteEventGenericEventRecord(T abstractTag, String relayUrl) {
    log.debug(STRING, Util.prettyPrintReferencedAbstractEventTag(abstractTag), relayUrl);

    Optional<GenericEventRecord> optionalGenericEventRecord = sendConsolidatorReq(
        relayUrl,
        getAbstractTagFilters(abstractTag));

    optionalGenericEventRecord.ifPresentOrElse(genericEventRecord ->
            log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()),
        () -> {
          log.debug("sendConsolidatorReq() did not find an event, throw NostrException");
          new NostrException("sendConsolidatorReq() did not find an event");
        });

    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);
    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()));

    return optionalGenericEventRecord;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull T abstractTag) {
    log.debug("inside getEvent(@NonNull T abstractTag) with abstractTag:{}", Util.prettyPrintReferencedAbstractEventTag(abstractTag));

    log.debug("... calling getLocalEventFxn(abstractTag) ...");
    Optional<GenericEventRecord> localGenericEventRecordOptional = getLocalEventFxn(abstractTag);

    if (localGenericEventRecordOptional.isPresent()) {
      log.debug("... returning local GenericEventRecord:{}", localGenericEventRecordOptional.get().createPrettyPrintJson());
      return localGenericEventRecordOptional;
    }

    String recommendedRelayUrl = Optional.ofNullable(abstractTag.getRelay().getUrl()).orElseThrow(() ->
        new NostrException(
            String.format("AbstractTag [%s] does not contain a (valid) url", abstractTag)));

    log.debug("local AbstractTag not found, calling getRemoteEventGenericEventRecord ...");
    return getRemoteEventGenericEventRecord(abstractTag, recommendedRelayUrl);
  }
}
