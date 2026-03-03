package com.prosilion.superconductor.autoconfigure.base.service.event.definition;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheDereferenceEventTagServiceIF;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.DurationFactory;
import org.springframework.lang.NonNull;

@Slf4j
public class CacheBadgeDefinitionGenericEventService implements CacheBadgeDefinitionGenericEventServiceIF {
  public static final String NON_EXISTENT_ADDRESS_TAG = "BadgeAwardGenericEvent [%s] is missing required AddressTag";
  public static final String NON_EXISTENT_DEFINITION_EVENT = "BadgeAwardGenericEvent [%s] contains AddressTag references non-existent BadgeDefinitionGenericEvent relay [%s]";
  private final CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF;
  private final CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF;

  public CacheBadgeDefinitionGenericEventService(
      @NonNull CacheDereferenceEventTagServiceIF cacheDereferenceEventTagServiceIF,
      @NonNull CacheDereferenceAddressTagServiceIF cacheDereferenceAddressTagServiceIF) {
    this.cacheDereferenceEventTagServiceIF = cacheDereferenceEventTagServiceIF;
    this.cacheDereferenceAddressTagServiceIF = cacheDereferenceAddressTagServiceIF;
  }

  @Override
  public Optional<BadgeDefinitionGenericEvent> getAddressTagEvent(@NonNull AddressTag addressTag, Duration timeout) throws JsonProcessingException {
    Optional<GenericEventRecord> unpopulatedBadgeDefinitionGenericEvent = cacheDereferenceAddressTagServiceIF.getEvent(addressTag, timeout);
    if (unpopulatedBadgeDefinitionGenericEvent.isEmpty())
      return Optional.empty();

    return Optional.of(materialize(unpopulatedBadgeDefinitionGenericEvent.get()));
  }

  @Override
  public BadgeDefinitionGenericEvent materialize(@NonNull EventIF eventIF) {
    Optional<BadgeDefinitionGenericEvent> badgeDefinitionGenericEvent = getPreExistingBadgeDefinitionGenericEvent(eventIF.asGenericEventRecord());
    return badgeDefinitionGenericEvent
        .orElseGet(() ->
            new BadgeDefinitionGenericEvent(eventIF.asGenericEventRecord()));
  }

  @SneakyThrows
  private Optional<BadgeDefinitionGenericEvent> getPreExistingBadgeDefinitionGenericEvent(@NonNull GenericEventRecord genericEventRecord) {
    RelayTag relayTag = Filterable.getTypeSpecificTagsStream(RelayTag.class, genericEventRecord)
        .findFirst().orElseThrow(() ->
            new NostrException(
                String.format(NON_EXISTENT_ADDRESS_TAG, genericEventRecord)));

    Optional<GenericEventRecord> firstAddressTagAsEvent = cacheDereferenceEventTagServiceIF.getEvent(
        genericEventRecord.getId(),
        relayTag.getRelay().getUrl(),
        DurationFactory.of(10, TimeUnit.SECONDS));

    if (firstAddressTagAsEvent.isEmpty())
      return Optional.empty();

    BadgeDefinitionGenericEvent addressTagNowBadgeDefinitionGenericEvent = new BadgeDefinitionGenericEvent(firstAddressTagAsEvent.get());

    return Optional.of(addressTagNowBadgeDefinitionGenericEvent);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
