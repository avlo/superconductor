package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.subdivisions.client.reactive.NostrRequestService;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceAddressTagService extends CacheDereferenceAbstractTagService<AddressTag> implements CacheDereferenceAddressTagServiceIF {
  public static final String FORMATTED_ADDRESS_TAG = "AddressTag[kind=%d, publicKey=%s, identifierTag=IdentifierTag[uuid=%s], relay=Relay[url=%s]]";

  public CacheDereferenceAddressTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl,
      @NonNull Duration requestTimeoutDuration,
      @NonNull NostrRequestService nostrRequestService) {
    super(cacheServiceIF, superconductorRelayUrl, requestTimeoutDuration, nostrRequestService);
  }

  @Override
  Optional<GenericEventRecord> getLocalEventFxn(AddressTag addressTag) {
    List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag = cacheServiceIF
        .getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
            addressTag.getKind(),
            addressTag.publicKey(),
            addressTag.getIdentifierTag());

    boolean hasContents = !eventsByKindAndAuthorPublicKeyAndIdentifierTag.isEmpty();
    if (hasContents) {
      log.debug("... returning local AddressTag: {}", pubKeySubstring(addressTag));
      return eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().findFirst();
    }

    return Optional.empty();
  }

  @Override
  Filters getAbstractTagFilters(AddressTag addressTag) {
    return new Filters(
        new KindFilter(addressTag.getKind()), // should always be 30009
        new AuthorFilter(addressTag.getPublicKey()),
        new IdentifierTagFilter(addressTag.getIdentifierTag()));
  }

  private String pubKeySubstring(AddressTag addressTag) {
    return String.format(FORMATTED_ADDRESS_TAG,
        addressTag.getKind().getValue(),
        addressTag.getPublicKey().toHexString().substring(0, 7).concat("..."),
        addressTag.getIdentifierTag().getUuid(),
        addressTag.getRelay().getUrl());
  }
}
