package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.filter.tag.ReferencedPublicKeyFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheKindAddressTagService implements CacheKindAddressTagServiceIF {
  protected final CacheServiceIF cacheServiceIF;
  private final RemoteAbstractTagService remoteAbstractTagService;

  public CacheKindAddressTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull RemoteAbstractTagService remoteAbstractTagService) {
    this.cacheServiceIF = cacheServiceIF;
    this.remoteAbstractTagService = remoteAbstractTagService;
  }

  @Override
  public List<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    List<GenericEventRecord> eventsByKindAndPubKeyTagAndAddressTag = cacheServiceIF.getEventsByKindAndPubKeyTagAndAddressTag(kind, pubKeyTag, addressTag);
    return eventsByKindAndPubKeyTagAndAddressTag;
  }

  @Override
  public Optional<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, @NonNull String relayUrl) {
    Optional<GenericEventRecord> localGenericEventRecordOptional = cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, pubKeyTag, identifierTag).stream().findFirst();

    if (localGenericEventRecordOptional.isPresent()) {
      log.debug("... localGenericEventRecordOptional found locally:\n  {}",
          localGenericEventRecordOptional.get().createPrettyPrintJson());
      return localGenericEventRecordOptional;
    }

    log.debug("local addressTag not found, calling getRemoteEventGenericEventRecord ...");
    Filters abstractTagFilters = new Filters(
        new KindFilter(kind),
        new ReferencedPublicKeyFilter(pubKeyTag),
        new IdentifierTagFilter(identifierTag));

    return getRemoteEventGenericEventRecords(abstractTagFilters, relayUrl).stream().findFirst();
  }

  @Override
  public List<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull AddressTag addressTag) {
    log.debug("inside getEventByKindAndAddressTag(Kind kind, AddressTag) with:\n  kind: [{}]\naddressTag:  {}", kind, Util.prettyPrintAddressTags(addressTag));
    log.debug("... calling getLocalEventFxn(kind, addressTag) ...");
    List<GenericEventRecord> localGenericEventRecords = cacheServiceIF.getEventsByKindAndAddressTag(kind, addressTag);

    if (!localGenericEventRecords.isEmpty()) {
      log.debug("... localGenericEventRecords found locally:\n  {}",
          localGenericEventRecords.stream().map(GenericEventRecord::createPrettyPrintJson));
      return localGenericEventRecords;
    }

    log.debug("local addressTag not found, calling getRemoteEventGenericEventRecord ...");
    Filters abstractTagFilters = getAbstractTagFilters(addressTag);
    abstractTagFilters.add(new KindFilter(kind));
    List<GenericEventRecord> remoteEventGenericEventRecords = getRemoteEventGenericEventRecords(
        abstractTagFilters,
        Optional.ofNullable(addressTag.getRelay().getUrl()).orElseThrow(() ->
            new NostrException(
                String.format("addressTag [%s] does not contain a (valid) url", addressTag))));
    return remoteEventGenericEventRecords;
  }

  private List<GenericEventRecord> getRemoteEventGenericEventRecords(Filters abstractTagFilters, String relayUrl) {
    log.debug("getRemoteEventGenericEventRecord filters:\n  {}", abstractTagFilters);
    List<GenericEventRecord> optionalGenericEventRecords = remoteAbstractTagService.sendRemoteReq(
        relayUrl,
        abstractTagFilters);

    optionalGenericEventRecords.forEach(cacheServiceIF::save);
    optionalGenericEventRecords.forEach(genericEventRecord ->
        log.debug("fetched remote event saved to local DB\n  {}", genericEventRecord.createPrettyPrintJson()));

    return optionalGenericEventRecords;
  }

  Filters getAbstractTagFilters(AddressTag addressTag) {
    return new Filters(new AddressTagFilter(addressTag));
  }
}
