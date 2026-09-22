package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardCanonicalEvent;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.IdentifierTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.PublishingEventKindPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class CuratedBadgeAwardCanonicalEventKindPlugin extends PublishingEventKindPlugin {
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;
  private final CacheServiceIF cacheServiceIF;

  public CuratedBadgeAwardCanonicalEventKindPlugin(
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull NotifierService notifierService,
     @NonNull EventPluginIF eventPluginIF,
     @NonNull CacheServiceIF cacheServiceIF) {
    super(notifierService, eventPluginIF);
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
    this.cacheServiceIF = cacheServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(
     @NonNull EventIF incomingCuratedBadgeAwardCanonicalEvent, @NonNull Relay fromRelay) {
    log.info("processIncomingEvent(incomingCuratedBadgeAwardCanonicalEvent, fromRelay) [{}]...\n{}", fromRelay.getUrl(), incomingCuratedBadgeAwardCanonicalEvent.createPrettyPrintJson());

    if (cacheServiceIF.getEventByEventId(incomingCuratedBadgeAwardCanonicalEvent.getId()).isPresent()) {
      log.info("return already existing identical incomingCuratedBadgeAwardCanonicalEvent");
      return Optional.of(incomingCuratedBadgeAwardCanonicalEvent.asGenericEventRecord());
    }

    RelayTag relayTag = incomingCuratedBadgeAwardCanonicalEvent.requireFirstTag(RelayTag.class);
    IdentifierTag suppliedIdentifierTag = incomingCuratedBadgeAwardCanonicalEvent.requireFirstTag(IdentifierTag.class);

    Optional<CuratedBadgeDefinitionGenericEvent> curatedBadgeDefinitionGenericEvent =
       cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent(
          suppliedIdentifierTag.getUuid(), relayTag.getRelay());

    if (curatedBadgeDefinitionGenericEvent.isEmpty()) {
      log.debug("non-existent curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent).  return Optional.empty()");
      return Optional.empty();
    }

    String guaranteedSourceRelayUrl = relayTag.getRelay().getUrl();
    CuratedBadgeAwardCanonicalEvent reconstructedCuratedBadgeAwardCanonicalEvent = new CuratedBadgeAwardCanonicalEvent(
       incomingCuratedBadgeAwardCanonicalEvent.asGenericEventRecord());
    log.debug("...done:\n{}", reconstructedCuratedBadgeAwardCanonicalEvent.createPrettyPrintJson());

    log.debug("saving CuratedBadgeAwardCanonicalEvent with guaranteedSourceRelayUrl as ReferenceTag URL: [{}]", guaranteedSourceRelayUrl);
    return super.processIncomingEvent(reconstructedCuratedBadgeAwardCanonicalEvent, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_AWARD_EVENT;
  }
}
