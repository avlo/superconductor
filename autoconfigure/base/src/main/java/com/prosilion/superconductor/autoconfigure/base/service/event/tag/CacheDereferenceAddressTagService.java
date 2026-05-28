package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheDereferenceAddressTagService extends CacheDereferenceAbstractTagService<AddressTag> implements CacheDereferenceAddressTagServiceIF {
  public static final String FORMATTED_ADDRESS_TAG = "AddressTag[kind=%d, publicKey=%s, identifierTag=IdentifierTag[uuid=%s], relay=Relay[url=%s]]";
  public static final String NON_EXISTENT_ADDRESS_TAG_EVENT = "AddressTag [%s] references non-existent Event";

  public CacheDereferenceAddressTagService(@NonNull CacheServiceIF cacheServiceIF) {
    super(cacheServiceIF);
  }

  @Override
  Optional<GenericEventRecord> getLocalEventFxn(AddressTag addressTag) {
    log.debug("inside getLocalEventFxn(AddressTag) with addressTag:\n  {}", Util.prettyPrintAddressTags(addressTag));

    List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag = cacheServiceIF
        .getEventsByKindAndAuthorPublicKeyAndIdentifierTag(
            addressTag.getKind(),
            addressTag.publicKey(),
            addressTag.getIdentifierTag());
    log.debug("found List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag size: [{}]",
        eventsByKindAndAuthorPublicKeyAndIdentifierTag.size());
    log.debug("found List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag:\n  {}",
        eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().map(GenericEventRecord::createPrettyPrintJson));

    if (!eventsByKindAndAuthorPublicKeyAndIdentifierTag.isEmpty()) {
      Optional<GenericEventRecord> first = eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().findFirst();
      log.debug("... returning first local AddressTag as GER:\n  {}", Util.prettyPrintGenericEventRecords(first.get()));
      return first;
    }

    log.debug("getLocalEventFxn(addressTag) not found, return EMPTY");
    return Optional.empty();
  }

  @Override
  public List<GenericEventRecord> getEventAddressTagsAsGenericEventRecords(@NonNull EventIF eventIF) {
    List<AddressTag> eventIFAddressTags = eventIF.getTypeSpecificTags(AddressTag.class);

    List<GenericEventRecord> genericEventRecords = eventIFAddressTags.stream()
        .map(addressTag1 -> getEvent(addressTag1)
            .orElseThrow(() ->
                new NostrException(String.format(NON_EXISTENT_ADDRESS_TAG_EVENT, addressTag1)))).toList();

    return genericEventRecords;
  }

  @Override
  Filters getAbstractTagFilters(AddressTag addressTag) {
    return new Filters(
        new KindFilter(addressTag.getKind()), // should always be 30009
        new AuthorFilter(addressTag.getPublicKey()),
        new IdentifierTagFilter(addressTag.getIdentifierTag()));
  }
}
