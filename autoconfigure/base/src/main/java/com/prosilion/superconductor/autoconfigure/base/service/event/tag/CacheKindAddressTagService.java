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
    log.debug("inside getBy(kind, pubKeyTag, identifierTag) with:\n  kind: [{}]\npubKeyTag:  [{}]\nidentifierTag:{}",
        kind,
        pubKeyTag.getPublicKey().toHexString(),
        identifierTag);

    log.debug("... calling cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, pubKeyTag, identifierTag) ...");
    List<GenericEventRecord> getByKindPubkeyTagIdentifierTagList = cacheServiceIF.getEventsByKindAndPubKeyTagAndIdentifierTag(kind, pubKeyTag, identifierTag);
    log.debug("cacheServiceIF returned kindPubkeyTagIdentifierTagList size:  [{}]", getByKindPubkeyTagIdentifierTagList.size());
    log.debug("cacheServiceIF returned kindPubkeyTagIdentifierTagList contents:\n  {}", getByKindPubkeyTagIdentifierTagList.stream().map(GenericEventRecord::createPrettyPrintJson));

    Optional<GenericEventRecord> firstGetByKindPubkeyTagIdentifierTag = getByKindPubkeyTagIdentifierTagList.stream().findFirst();
    if (firstGetByKindPubkeyTagIdentifierTag.isPresent()) {
      log.debug("... returning firstGetByKindPubkeyTagIdentifierTag found locally:\n  {}", firstGetByKindPubkeyTagIdentifierTag.get().createPrettyPrintJson());
      return firstGetByKindPubkeyTagIdentifierTag;
    }

    Filters abstractTagFilters = new Filters(
        new KindFilter(kind),
        new ReferencedPublicKeyFilter(pubKeyTag),
        new IdentifierTagFilter(identifierTag));
    log.debug("no local kindPubkeyTagIdentifierTag match, call remote w/ filters:{}", abstractTagFilters.toString(2));

    return getRemoteEventGenericEventRecords(abstractTagFilters, relayUrl).stream().findFirst();
  }

  @Override
  public List<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull AddressTag addressTag) {
    log.debug("inside getBy(kind, AddressTag) with:\n  kind: [{}]\naddressTag:\n    {}", kind, addressTag.toStringPrettyPrint());
    
    log.debug("... calling cacheServiceIF.getEventsByKindAndAddressTag(kind, addressTag) ...");
    List<GenericEventRecord> getByKindAddressTagList = cacheServiceIF.getEventsByKindAndAddressTag(kind, addressTag);
    log.debug("cacheServiceIF returned getByKindAddressTagList size:  [{}]", getByKindAddressTagList.size());
    log.debug("cacheServiceIF returned getByKindAddressTagList contents:\n  {}", getByKindAddressTagList.stream().map(GenericEventRecord::createPrettyPrintJson));

    if (!getByKindAddressTagList.isEmpty()) {
      log.debug("... return getByKindAddressTagList found locally:\n  {}",
          getByKindAddressTagList.stream().map(GenericEventRecord::createPrettyPrintJson));
      return getByKindAddressTagList;
    }

    Filters abstractTagFilters = getAbstractTagFilters(addressTag);
    abstractTagFilters.add(new KindFilter(kind));
    log.debug("no local kindAddressTag found, call remote w/ filters:\n{}", abstractTagFilters.toString(4));

    List<GenericEventRecord> remoteEventGenericEventRecords = getRemoteEventGenericEventRecords(
        abstractTagFilters,
        Optional.ofNullable(addressTag.getRelay().getUrl()).orElseThrow(() ->
            new NostrException(
                String.format("addressTag [%s] does not contain a (valid) url", addressTag))));
    return remoteEventGenericEventRecords;
  }

  private List<GenericEventRecord> getRemoteEventGenericEventRecords(Filters abstractTagFilters, String relayUrl) {
    log.debug("getRemoteEventGenericEventRecord filters:\n  {}", abstractTagFilters.toString(4));
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
