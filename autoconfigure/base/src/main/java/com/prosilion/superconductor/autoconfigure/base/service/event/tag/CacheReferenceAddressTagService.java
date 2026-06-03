package com.prosilion.superconductor.autoconfigure.base.service.event.tag;

import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filters;
import com.prosilion.nostr.filter.event.AuthorFilter;
import com.prosilion.nostr.filter.event.KindFilter;
import com.prosilion.nostr.filter.tag.IdentifierTagFilter;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceAddressTagServiceIF;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheReferenceAddressTagService extends CacheReferenceAbstractTagService<AddressTag> implements CacheReferenceAddressTagServiceIF {
  public CacheReferenceAddressTagService(
      @NonNull CacheServiceIF cacheServiceIF,
      @NonNull RemoteAbstractTagService remoteAbstractTagService) {
    super(cacheServiceIF, remoteAbstractTagService);
  }

  @Override
  Optional<GenericEventRecord> getLocalEventFxn(@NonNull AddressTag addressTag) {
    log.debug("inside getLocalEventFxn(AddressTag) with addressTag:{}", Util.prettyPrintAddressTags(addressTag));

    Optional<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag = cacheServiceIF
        .getEventByKindAndAuthorPublicKeyAndIdentifierTag(
            addressTag.getKind(),
            addressTag.publicKey(),
            addressTag.getIdentifierTag());
    log.debug("received List<GenericEventRecord> eventsByKindAndAuthorPublicKeyAndIdentifierTag:{}",
        eventsByKindAndAuthorPublicKeyAndIdentifierTag
            .map(GenericEventRecord::createPrettyPrintJson)
            .map(s -> Strings.concat("\n  ", s))
            .orElse("[EMPTY OPTIONAL]"));

    if (!eventsByKindAndAuthorPublicKeyAndIdentifierTag.isEmpty()) {
      Optional<GenericEventRecord> first = eventsByKindAndAuthorPublicKeyAndIdentifierTag.stream().findFirst();
      log.debug("... returning first local AddressTag as GER:\n  {}", Util.prettyPrintGenericEventRecords(first.get()));
      return first;
    }

    log.debug("getLocalEventFxn(addressTag) not found, return EMPTY");
    return Optional.empty();
  }

  @Override
  Filters getAbstractTagFilters(@NonNull AddressTag addressTag) {
    return new Filters(
        new KindFilter(addressTag.getKind()), // should always be 30009
        new AuthorFilter(addressTag.getPublicKey()),
        new IdentifierTagFilter(addressTag.getIdentifierTag()));
  }
}
