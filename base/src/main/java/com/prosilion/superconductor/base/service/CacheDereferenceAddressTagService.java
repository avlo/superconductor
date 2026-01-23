package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.tag.AddressTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceAddressTagService implements CacheDereferenceAddressTagServiceIF {
  CacheServiceIF cacheServiceIF;

  public CacheDereferenceAddressTagService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull AddressTag addressTag) {
    return Optional.ofNullable(
            addressTag.getRelay()).map(Relay::getUrl)
        .flatMap(url ->
            cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
                    addressTag.getKind(),
                    addressTag.publicKey(),
                    addressTag.getIdentifierTag()).stream().findFirst()
                .or(
                    remoteEventSupplier(url, addressTag, fxnAddressTag ->
                        new Filters(new AddressTagFilter(addressTag)))));

  }

}
