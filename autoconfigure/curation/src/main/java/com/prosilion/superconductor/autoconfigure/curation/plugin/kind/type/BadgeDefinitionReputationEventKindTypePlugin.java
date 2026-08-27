package com.prosilion.superconductor.autoconfigure.curation.plugin.kind.type;

import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.curated.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.curated.CuratedFormulaEvent;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.util.Util;
import com.prosilion.superconductor.autoconfigure.curation.service.CacheCuratedFormulaEventServiceIF;
import com.prosilion.superconductor.base.cache.CacheServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.NonPublishingEventKindTypePlugin;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionReputationEventKindTypePlugin extends NonPublishingEventKindTypePlugin {
  private final CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF;
  CacheServiceIF cacheServiceIF;

  public BadgeDefinitionReputationEventKindTypePlugin(
     @NonNull String superconductorRelayUrl,
     @NonNull CacheCuratedFormulaEventServiceIF cacheCuratedFormulaEventServiceIF,
     @NonNull EventKindTypePluginIF eventKindTypePlugin,
     CacheServiceIF cacheServiceIF) {
    super(eventKindTypePlugin);
    this.cacheCuratedFormulaEventServiceIF = cacheCuratedFormulaEventServiceIF;
    this.cacheServiceIF = cacheServiceIF;
    Util.debug(log, "using superconductorRelayUrl: [{}]", superconductorRelayUrl, true, '0');
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF incomingBadgeDefinitionReputationEvent, @NonNull Relay fromRelay) {
    log.debug("processIncomingEvent(incomingBadgeDefinitionReputationEvent, fromRelay) [{}]...\n{}", fromRelay.getUrl(), incomingBadgeDefinitionReputationEvent.createPrettyPrintJson());

    Optional<RelayTag> eventRelayTag = incomingBadgeDefinitionReputationEvent.findFirstTag(RelayTag.class);
    log.debug("processing incoming BadgeDefinitionReputationEvent using incomingBadgeDefinitionReputationEvent RelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));

    List<CuratedFormulaEvent> curatedFormulaEventList =
       incomingBadgeDefinitionReputationEvent.getTypeSpecificTags(AddressTag.class).stream().map(addressTag ->
          cacheCuratedFormulaEventServiceIF.getByAuthorAndIdentifierTag(
             addressTag.getPublicKey(),
             addressTag.getIdentifierTag())).flatMap(Optional::stream).toList();

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
       incomingBadgeDefinitionReputationEvent.asGenericEventRecord(),
       addressTag -> curatedFormulaEventList.stream()
          .filter(newCuratedFormulaEvent ->
             newCuratedFormulaEvent.asAddressableEventAddressTag().equals(addressTag)).findFirst().orElseThrow());

    return super.processIncomingEvent(badgeDefinitionReputationEvent, fromRelay);
  }
}
