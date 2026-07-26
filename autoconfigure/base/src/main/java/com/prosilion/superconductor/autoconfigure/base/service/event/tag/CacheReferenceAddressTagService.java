package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.RemoteEventQueryServiceIF;
import java.util.Optional;
import lombok.NonNull;

public class CacheReferenceAddressTagService extends CacheReferenceAbstractTagService<AddressTag> implements CacheReferenceAddressTagServiceIF {
  public CacheReferenceAddressTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    super(cacheServiceIF, remoteEventQueryServiceIF);
  }

  @Override
  protected Optional<GenericEventRecord> tryGetLocalExpandedEvent(@NonNull AddressTag addressTag) {
    return cacheServiceIF
       .getEventByKindAndAuthorPublicKeyAndIdentifierTag(
          addressTag.getKind(),
          addressTag.publicKey(),
          addressTag.requireIdentifierTag());
  }

  @Override
  protected Filters createFilters(@NonNull AddressTag addressTag) {
    return TagFilterFactory.forAddressIdentity(addressTag);
  }
}
