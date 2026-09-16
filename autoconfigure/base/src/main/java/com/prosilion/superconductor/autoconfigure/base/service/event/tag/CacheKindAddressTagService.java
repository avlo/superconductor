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

// TODO: determine whether/not to save/curate retrieved items / alternate sol'n
public class CacheKindAddressTagService implements CacheKindAddressTagServiceIF {
  private final CacheServiceIF cacheServiceIF;
  private final RemoteEventQueryServiceIF remoteEventQueryServiceIF;

  public CacheKindAddressTagService(
     @NonNull CacheServiceIF cacheServiceIF,
     @NonNull RemoteEventQueryServiceIF remoteEventQueryServiceIF) {
    this.cacheServiceIF = cacheServiceIF;
    this.remoteEventQueryServiceIF = remoteEventQueryServiceIF;
  }

  @Override
  public List<GenericEventRecord> getByDirect(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    return findLocalByKindPubKeyAndAddress(kind, pubKeyTag, addressTag);
  }

  @Override
  public Optional<GenericEventRecord> getBy(@NonNull Kind kind, @NonNull PubKeyTag pubKeyTag, @NonNull IdentifierTag identifierTag, @NonNull String relayUrl) {
    return findLocalByKindPubKeyAndIdentifier(kind, pubKeyTag, identifierTag)
       .or(() ->
          remoteEventQueryServiceIF.sendRemoteReq(
             relayUrl,
             TagFilterFactory.forReferencedPubKeyAndIdentifier(kind, pubKeyTag, identifierTag)).stream().findFirst());
  }

  @Override
  public List<GenericEventRecord> getByDirect(@NonNull Kind kind, @NonNull AddressTag addressTag) {
    List<GenericEventRecord> genericEventRecordList = findLocalByKindAndAddress(kind, addressTag);
    return
       genericEventRecordList.isEmpty() ?
          remoteEventQueryServiceIF.sendRemoteReq(
             addressTag.requireRelay().getUrl(),
             TagFilterFactory.forAddressReference(kind, addressTag))
          :
          genericEventRecordList;
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
