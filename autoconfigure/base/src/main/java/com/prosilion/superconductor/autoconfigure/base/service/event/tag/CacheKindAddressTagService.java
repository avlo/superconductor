package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.RemoteEventQueryServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;

public class CacheKindAddressTagService implements CacheKindAddressTagServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final CacheAsideEventLookup cacheAsideEventLookup;

  public CacheKindAddressTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.cacheAsideEventLookup = new CacheAsideEventLookup(remoteEventQueryServiceIF);
  }

  @Override
  public List<GenericEventRecord> getByDirect(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return findLocalByKindPubKeyAndAddress(kind, pubKeyTag, addressTag);
  }

  @Override
  public Optional<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, @NonNull String relayUrl) {
    return cacheAsideEventLookup.findFirst(
       () -> findLocalByKindPubKeyAndIdentifier(kind, pubKeyTag, identifierTag),
       relayUrl,
       TagFilterFactory.forReferencedPubKeyAndIdentifier(kind, pubKeyTag, identifierTag));
  }

  @Override
  public List<GenericEventRecord> getByDirect(@NonNull Kind kind, @NonNull AddressTag addressTag) {
    return cacheAsideEventLookup.findAll(
       () -> findLocalByKindAndAddress(kind, addressTag),
       addressTag.requireRelay().getUrl(),
       TagFilterFactory.forAddressReference(kind, addressTag));
  }

  private List<GenericEventRecord> findLocalByKindPubKeyAndAddress(
     Kind kind,
     PubKeyTag pubKeyTag,
     AddressTag addressTag) {
    return cacheServiceIF.getEventsByKindAndPubKeyTagAndAddressTag(kind, pubKeyTag, addressTag);
  }

  private Optional<GenericEventRecord> findLocalByKindPubKeyAndIdentifier(
     Kind kind,
     PubKeyTag pubKeyTag,
     IdentifierTag identifierTag) {
    return cacheServiceIF
       .getEventsByKindAndPubKeyTagAndIdentifierTag(kind, pubKeyTag, identifierTag)
       .stream()
       .findFirst();
  }

  private List<GenericEventRecord> findLocalByKindAndAddress(Kind kind, AddressTag addressTag) {
    return cacheServiceIF.getEventsByKindAndAddressTag(kind, addressTag);
  }
}
