package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
    List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag = cacheServiceIF
        .getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
            addressTag.getKind(),
            addressTag.publicKey(),
            addressTag.getIdentifierTag());

    boolean hasContents = !eventsByKindAndAuthorPublicKeyAndIdentifierTag.isEmpty();
    if (hasContents)
      return eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().findFirst();

    return getGenericEventRecord(addressTag);
  }

  private Optional<GenericEventRecord> getGenericEventRecord(AddressTag addressTag) {
    String recommendedRelayUrl = Optional.ofNullable(
        addressTag.getRelay()).map(Relay::getUrl).orElseThrow(() ->
        new NostrException(
            String.format("AddressTag [%s] does not contain a (valid) url", addressTag)));

    Function<AddressTag, Filters> addressTagFiltersFunction = fxnAddressTag ->
        new Filters(
            new KindFilter(addressTag.getKind()), // should always be 30009
            new AuthorFilter(addressTag.getPublicKey()),
            new IdentifierTagFilter(addressTag.getIdentifierTag()));

    Optional<GenericEventRecord> optionalGenericEventRecord =
        remoteEventSupplier(
            recommendedRelayUrl,
            addressTag,
            addressTagFiltersFunction);

    return optionalGenericEventRecord;
  }
}
