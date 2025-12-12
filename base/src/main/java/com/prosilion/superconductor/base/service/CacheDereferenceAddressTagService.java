package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.Optional;
import org.springframework.lang.NonNull;

public class CacheDereferenceAddressTagService implements CacheDereferenceAddressTagServiceIF {
  CacheServiceIF cacheServiceIF;

  public CacheDereferenceAddressTagService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull AddressTag addressTag) {
    return Optional.ofNullable(
        cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
            addressTag.getKind(),
            addressTag.publicKey(),
            addressTag.getIdentifierTag()).getFirst());
  }
}
