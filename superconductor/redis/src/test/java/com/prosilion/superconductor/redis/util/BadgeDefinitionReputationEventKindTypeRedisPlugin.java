package com.prosilion.superconductor.redis.util;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionReputationEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionReputationEventServiceIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.EventKindTypePluginIF;
import com.prosilion.superconductor.base.service.event.plugin.kind.type.NonPublishingEventKindTypePlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionReputationEventKindTypeRedisPlugin extends NonPublishingEventKindTypePlugin {
  CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService;
  private final String superconductorRelayUrl;

  public BadgeDefinitionReputationEventKindTypeRedisPlugin(
      @NonNull String superconductorRelayUrl,
      @NonNull EventKindTypePluginIF eventKindTypePlugin,
      @NonNull CacheBadgeDefinitionReputationEventServiceIF cacheBadgeDefinitionReputationEventService) {
    super(eventKindTypePlugin);
    this.cacheBadgeDefinitionReputationEventService = cacheBadgeDefinitionReputationEventService;
    this.superconductorRelayUrl = superconductorRelayUrl;
  }

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    String eventRelaysTagUrl = Filterable.getTypeSpecificTagsStream(RelayTag.class, event)
        .map(RelayTag::getRelay)
        .map(Relay::getUrl)
        .findAny().orElseThrow(() ->
            new NostrException(
                String.format("BadgeDefinitionEvent\n  %s\nmissing required RelayTag", event.createPrettyPrintJson())));

    if (!superconductorRelayUrl.equals(eventRelaysTagUrl))
      throw new NostrException(
          String.format("RelayTag URL: [%s] does not match relay host SuperConductor URL: [%s]",
              eventRelaysTagUrl, superconductorRelayUrl));

    return super.processIncomingEvent(cacheBadgeDefinitionReputationEventService.materialize(event));
  }

  @Override
  public BadgeDefinitionReputationEvent materialize(EventIF eventIF) {
    return cacheBadgeDefinitionReputationEventService.materialize(eventIF);
  }
}
