package com.prosilion.superconductor.base.service;

import com.prosilion.nostr.event.GenericEventId;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.EventFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.EventTag;
import com.prosilion.superconductor.base.service.event.CacheServiceIF;
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
    Optional<GenericEventRecord> eventByAddressTag = cacheServiceIF.getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
        addressTag.getKind(),
        addressTag.publicKey(),
        addressTag.getIdentifierTag()).stream().findFirst();

//    Function<AddressTag, Filters> filterFxn = fxnAddressTag ->
//        new Filters(new AddressTagFilter(addressTag));

    String eventIdFromRetrievedAddressTag =
        eventByAddressTag.map(ger -> {
              log.debug("id from generic event record");
              return ger.getId();
            })
            .orElseGet(this::getAsdafds);
    String urlFromRetrievedAddressTag = Optional.ofNullable(addressTag.getRelay()).orElseThrow().getUrl();

    Function<EventTag, Filters> filterFxn = fxnEventTag ->
        getFilters(eventIdFromRetrievedAddressTag);

    EventTag eventTag = new EventTag(
        eventIdFromRetrievedAddressTag,
        urlFromRetrievedAddressTag);

    Optional<GenericEventRecord> genericEventRecord =
        eventByAddressTag
            .or(
                remoteEventSupplier(
                    urlFromRetrievedAddressTag,
                    eventTag,
                    filterFxn));
    return genericEventRecord;
  }

  private String getAsdafds() {
    log.debug("id from generic event record");
    return "e003dfa8d5bd10810833faf4d964d65ee41509bb42cf6d5412c97556a2a133c5";
  }

  private Filters getFilters(String eventIdFromRetrievedAddressTag) {
    Filters filters = new Filters(new EventFilter(new GenericEventId(eventIdFromRetrievedAddressTag)));
    return filters;
  }
}
