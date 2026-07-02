package com.prosilion.superconductor.autoconfigure.base.service.event.award;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeAwardGenericEventAux;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.superconductor.base.cache.CacheBadgeAwardGenericEventAuxServiceIF;
import com.prosilion.superconductor.base.cache.tag.CacheReferenceEventTagServiceIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheBadgeAwardGenericEventAuxService implements CacheBadgeAwardGenericEventAuxServiceIF {
  private final CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService;
  private final CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF;

  public CacheBadgeAwardGenericEventAuxService(
     @NonNull CacheReferenceEventTagServiceIF cacheReferenceEventTagServiceIF,
     @NonNull CacheBadgeAwardGenericEventService cacheBadgeAwardGenericEventService) {
    this.cacheReferenceEventTagServiceIF = cacheReferenceEventTagServiceIF;
    this.cacheBadgeAwardGenericEventService = cacheBadgeAwardGenericEventService;
  }

  @Override
  public Optional<BadgeAwardGenericEventAux> materialize(@NonNull EventIF incomingBadgeAwardGenericEvent, Relay relay) {
    log.debug("... materialize incomingBadgeAwardGenericEvent:\n{}", incomingBadgeAwardGenericEvent.createPrettyPrintJson());

    Optional<BadgeAwardGenericEvent<BadgeDefinitionGenericEvent>> materialize = cacheBadgeAwardGenericEventService.materialize(incomingBadgeAwardGenericEvent);

    Optional<BadgeAwardGenericEventAux> badgeAwardGenericEventAux = materialize.map(event ->
       new BadgeAwardGenericEventAux(
          event,
          event.getRelay().orElse(relay)));

    return badgeAwardGenericEventAux;
  }

  public Optional<BadgeAwardGenericEventAux> getEvent(@NonNull String eventId, Relay relay) {
    log.debug("... calling cacheReferenceEventTagServiceIF.getEvent(eventId, url): [{}], [{}]", eventId, relay.getUrl());

    Optional<GenericEventRecord> unpopulatedEvent = cacheReferenceEventTagServiceIF.getEvent(eventId, relay);
    log.debug("returned pre-materialized optGER:\n{}",
       unpopulatedEvent.map(GenericEventRecord::createPrettyPrintJson).orElse("EMPTY OPTIONAL"));

    Optional<BadgeAwardGenericEventAux> badgeAwardGenericEventAux = unpopulatedEvent.flatMap(event -> materialize(event, relay));

    return badgeAwardGenericEventAux;
  }

  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
