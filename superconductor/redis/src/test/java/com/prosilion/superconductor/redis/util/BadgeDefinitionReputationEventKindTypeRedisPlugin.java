package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FormulaEvent;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.AddressTag;
import com.prosilion.superconductor.base.service.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.event.service.plugin.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.type.NonPublishingEventKindTypePlugin;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionReputationEventKindTypeRedisPlugin extends NonPublishingEventKindTypePlugin {
  CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService;

  public BadgeDefinitionReputationEventKindTypeRedisPlugin(
      @NonNull EventKindTypePluginIF eventKindTypePlugin,
      @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService) {
    super(eventKindTypePlugin);
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF incomingBadgeDefinitionReputationEvent) {
    log.debug("processing incoming EventIF as FORMULA EVENT: [{}]", incomingBadgeDefinitionReputationEvent);

    List<FormulaEvent> formulaEvents = cacheBadgeDefinitionReputationEventService.getFormulaEvents((GenericEventRecord) incomingBadgeDefinitionReputationEvent);

    Function<AddressTag, FormulaEvent> fxn = addressTag ->
        formulaEvents.stream().filter(formulaEvent ->
            formulaEvent.getIdentifierTag().equals(addressTag.getIdentifierTag())).findFirst().orElseThrow();

    BadgeDefinitionReputationEvent badgeDefinitionReputationEvent = new BadgeDefinitionReputationEvent(
        (GenericEventRecord) incomingBadgeDefinitionReputationEvent,
        fxn);

    BadgeDefinitionReputationEvent reconstructedBadgeDefinitionReputationEvent = cacheBadgeDefinitionReputationEventService.reconstruct(badgeDefinitionReputationEvent);
    super.processIncomingEvent(reconstructedBadgeDefinitionReputationEvent);
  }
}
