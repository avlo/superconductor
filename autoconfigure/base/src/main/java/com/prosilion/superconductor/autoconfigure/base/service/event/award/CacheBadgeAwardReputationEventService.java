package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardReputationEvent;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.ExternalIdentityTag;
import com.prosilion.nostr.tag.PubKeyTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.base.service.event.definition.CacheBadgeDefinitionReputationEventService;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardReputationEventServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheKindAddressTagServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

// TODO: likely replaceable by CacheBadgeAwardGenericEventService
@Slf4j
public class CacheBadgeAwardReputationEventService extends CacheBadgeAwardAbstractEventService<BadgeDefinitionReputationEvent, BadgeAwardReputationEvent> implements CacheBadgeAwardReputationEventServiceIF {
  private final CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF;
  private final CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService;

  public CacheBadgeAwardReputationEventService(
      @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
      @NonNull CacheKindAddressTagServiceIF cacheKindAddressTagServiceIF,
      @NonNull CacheBadgeDefinitionReputationEventService cacheBadgeDefinitionReputationEventService) {
    super(cacheReferenceEventTagServiceIF);
    this.cacheKindAddressTagServiceIF = cacheKindAddressTagServiceIF;
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public BadgeAwardReputationEvent materialize(@NonNull EventIF incomingBadgeAwardReputationEvent) {
    log.debug("... materialize incomingBadgeAwardReputationEvent:\n{}", incomingBadgeAwardReputationEvent.createPrettyPrintJson());

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = cacheBadgeDefinitionReputationEventService.getBy(
            incomingBadgeAwardReputationEvent.requireFirstTag(PubKeyTag.class),
            incomingBadgeAwardReputationEvent.requireFirstTag(AddressTag.class))
        .orElseThrow(() ->
            new NostrException("badgeDefinitionReputationEvent not found"));

    BadgeAwardReputationEvent badgeAwardReputationEvent = new BadgeAwardReputationEvent(
        incomingBadgeAwardReputationEvent.asGenericEventRecord(),
        addressTag -> badgeDefinitionReputationEvent);
    log.debug("... return materialized badgeAwardReputationEvent:\n{}", badgeAwardReputationEvent.createPrettyPrintJson());

    return badgeAwardReputationEvent;
  }

  @Override
  public Optional<BadgeAwardReputationEvent> getBy(@NonNull PubKeyTag pubKeyTag, @NonNull AddressTag addressTag) {
    List<GenericEventRecord> addressTagsAsGenericEventRecord =
        cacheKindAddressTagServiceIF.getBy(
            Kind.BADGE_AWARD_EVENT,
            pubKeyTag,
            addressTag);

    if (addressTagsAsGenericEventRecord.isEmpty()) {
      log.debug("... BadgeAwardReputationEvent does not exist- getBy(pubKeyTag, addressTag) was EMPTY");
      return Optional.empty();
    }

    Optional<GenericEventRecord> badgeAwardReputationEventOptGER = addressTagsAsGenericEventRecord.stream()
        .filter(ger ->
            ger.findFirstTag(ExternalIdentityTag.class).isPresent()).findFirst();

    Optional<BadgeAwardReputationEvent> badgeAwardReputationEventOpt = badgeAwardReputationEventOptGER.map(genericEventRecord ->
        getEvent(
            genericEventRecord.getId(),
            genericEventRecord.requireFirstTag(RelayTag.class).getRelay().getUrl()).orElseThrow());

    log.debug("... return materialized badgeAwardReputationEvent:\n{}", badgeAwardReputationEventOpt.map(EventIF::createPrettyPrintJson).orElse("EMPTY Optional"));
    return badgeAwardReputationEventOpt;
  }
}
