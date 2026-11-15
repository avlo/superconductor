package com.prosilion.superconductor.autoconfigure.curation.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.CuratedBadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedBadgeDefinitionGenericEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.NonPublishingEventKindPlugin;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
// our SportsCar extends CarDecorator
public class CuratedBadgeDefinitionGenericEventKindPlugin extends NonPublishingEventKindPlugin {
  private final CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF;

  public CuratedBadgeDefinitionGenericEventKindPlugin(
     @NonNull CacheCuratedBadgeDefinitionGenericEventServiceIF cacheCuratedBadgeDefinitionGenericEventServiceIF,
     @NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
    this.cacheCuratedBadgeDefinitionGenericEventServiceIF = cacheCuratedBadgeDefinitionGenericEventServiceIF;
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    log.info("processIncomingEvent(event, fromRelay) [{}]...\n{}", fromRelay.getUrl(), event.createPrettyPrintJson());

    Optional<RelayTag> eventRelayTag = event.findFirstTag(RelayTag.class);
    log.debug("processing incoming BadgeDefinitionGenericEvent using event RelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));

    Relay guaranteedSourceRelay = eventRelayTag.map(RelayTag::getRelay).orElse(fromRelay);

    if (cacheCuratedBadgeDefinitionGenericEventServiceIF.getEvent(event.getId(), guaranteedSourceRelay).isPresent()) {
      log.info("return already existing identical CuratedBadgeDefinitionGenericEvent");
      return Optional.of(event.asGenericEventRecord());
    }

    CuratedBadgeDefinitionGenericEvent curatedBadgeDefinitionGenericEvent =
       new CuratedBadgeDefinitionGenericEvent(event.asGenericEventRecord());

    log.debug("saving CuratedBadgeDefinitionGenericEvent with guaranteedSourceRelayUrl as ReferenceTag URL: [{}]", guaranteedSourceRelay);
    return super.processIncomingEvent(curatedBadgeDefinitionGenericEvent, guaranteedSourceRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.CURATION_SETS_BADGE_DEFINITION_EVENT;
  }
}
