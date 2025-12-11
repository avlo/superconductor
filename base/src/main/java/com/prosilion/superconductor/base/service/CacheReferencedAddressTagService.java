package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import org.springframework.lang.NonNull;

public class CacheReferencedAddressTagService implements CacheReferencedAddressTagServiceIF {
  CacheServiceIF cacheServiceIF;

  public CacheReferencedAddressTagService(@NonNull CacheServiceIF cacheServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull AddressTag addressTag) {
    List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
        addressTag.getKind(),
        addressTag.publicKey(),
        addressTag.getIdentifierTag());
    Optional<GenericEventRecord> first = Optional.ofNullable(eventsByKindAndAuthorPublicKeyAndIdentifierTag.getFirst());
    return first;
  }
}
