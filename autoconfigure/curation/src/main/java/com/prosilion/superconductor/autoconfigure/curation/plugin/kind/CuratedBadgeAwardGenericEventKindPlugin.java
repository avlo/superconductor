package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeAwardGenericEvent;
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
public class CuratedBadgeAwardGenericEventKindPlugin extends PublishingEventKindPlugin {
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;
  private final CacheServiceIF cacheServiceIF;

  public CuratedBadgeAwardGenericEventKindPlugin(
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
     @NonNull EventIF incomingCuratedBadgeAwardGenericEvent, @NonNull Relay fromRelay) {
    log.info("processIncomingEvent(incomingCuratedBadgeAwardGenericEvent, fromRelay) [{}]...\n{}", fromRelay.getUrl(), incomingCuratedBadgeAwardGenericEvent.createPrettyPrintJson());

    if (cacheServiceIF.getEventByEventId(incomingCuratedBadgeAwardGenericEvent.getId()).isPresent()) {
      log.info("return already existing identical incomingCuratedBadgeAwardGenericEvent");
      return Optional.of(incomingCuratedBadgeAwardGenericEvent.asGenericEventRecord());
    }

    RelayTag relayTag = incomingCuratedBadgeAwardGenericEvent.requireFirstTag(RelayTag.class);
    IdentifierTag suppliedIdentifierTag = incomingCuratedBadgeAwardGenericEvent.requireFirstTag(IdentifierTag.class);

    Optional<CuratedBadgeDefinitionGenericEvent> curatedBadgeDefinitionGenericEvent =
       cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent(
          suppliedIdentifierTag.getUuid(), relayTag.getRelay());

    if (curatedBadgeDefinitionGenericEvent.isEmpty()) {
      log.debug("non-existent curatedBadgeDefinitionGenericEvent (and therefore, badgeDefinitionGenericEvent).  return Optional.empty()");
      return Optional.empty();
    }

    String guaranteedSourceRelayUrl = relayTag.getRelay().getUrl();
    CuratedBadgeAwardGenericEvent reconstructedCuratedBadgeAwardGenericEvent = new CuratedBadgeAwardGenericEvent(
       incomingCuratedBadgeAwardGenericEvent.asGenericEventRecord());
    log.debug("...done:\n{}", reconstructedCuratedBadgeAwardGenericEvent.createPrettyPrintJson());

    log.debug("saving CuratedBadgeAwardGenericEvent with guaranteedSourceRelayUrl as ReferenceTag URL: [{}]", guaranteedSourceRelayUrl);
    return super.processIncomingEvent(reconstructedCuratedBadgeAwardGenericEvent, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_AWARD_EVENT;
  }
}
