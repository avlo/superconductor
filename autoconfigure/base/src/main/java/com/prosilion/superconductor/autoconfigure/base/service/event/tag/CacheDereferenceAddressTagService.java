package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceAddressTagService implements CacheDereferenceAddressTagServiceIF {
  public static final String FORMATTED_ADDRESS_TAG = "AddressTag[kind=%d, publicKey=%s, identifierTag=IdentifierTag[uuid=%s], relay=Relay[url=%s]]";
  private final CacheServiceIF cacheServiceIF;

  @Getter
  private final String superconductorRelayUrl;

  public CacheDereferenceAddressTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull String superconductorRelayUrl) {
    this.cacheServiceIF = cacheServiceIF;
    this.superconductorRelayUrl = superconductorRelayUrl;
  }

  @Override
  public Optional<GenericEventRecord> getEvent(@NonNull AddressTag addressTag) {
    log.debug("getEvent(AddressTag): {}", pubKeySubstring(addressTag));

    List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag = cacheServiceIF
        .getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
            addressTag.getKind(),
            addressTag.publicKey(),
            addressTag.getIdentifierTag());

    boolean hasContents = !eventsByKindAndAuthorPublicKeyAndIdentifierTag.isEmpty();
    if (hasContents) {
      log.debug("... returning local AddressTag: {}\n", pubKeySubstring(addressTag));
      return eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().findFirst();
    }

    log.debug("local AddressTag not found, calling remoteEventSupplier...");
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

    optionalGenericEventRecord.ifPresent(genericEventRecord ->
        log.debug("fetched remote event {} saved to local DB", genericEventRecord));
    optionalGenericEventRecord.ifPresent(cacheServiceIF::save);

    return optionalGenericEventRecord;
  }

  private String pubKeySubstring(AddressTag addressTag) {
    return String.format(FORMATTED_ADDRESS_TAG,
        addressTag.getKind().getValue(),
        addressTag.getPublicKey().toHexString().substring(0, 7).concat("..."),
        addressTag.getIdentifierTag().getUuid(),
        addressTag.getRelay().getUrl());
  }
}
