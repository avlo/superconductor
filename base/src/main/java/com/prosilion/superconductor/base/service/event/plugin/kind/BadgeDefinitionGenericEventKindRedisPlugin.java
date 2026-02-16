package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.BaseEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.cache.CacheBadgeDefinitionGenericEventServiceIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionGenericEventKindRedisPlugin extends NonPublishingEventKindPlugin {
  private final CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF;
  private final String superconductorRelayUrl;

  public BadgeDefinitionGenericEventKindRedisPlugin(
      @NonNull String superconductorRelayUrl,
      @NonNull EventKindPluginIF eventKindPluginIF,
      @NonNull CacheBadgeDefinitionGenericEventServiceIF cacheBadgeDefinitionGenericEventServiceIF) {
    super(eventKindPluginIF);
    this.cacheBadgeDefinitionGenericEventServiceIF = cacheBadgeDefinitionGenericEventServiceIF;
    this.superconductorRelayUrl = superconductorRelayUrl;
  }

  @Override
  public <T extends BaseEvent> void processIncomingEvent(@NonNull T event) {
    String eventRelaysTagUrl = event.getTypeSpecificTags(RelayTag.class).stream()
        .map(RelayTag::getRelay)
        .map(Relay::getUrl)
        .findAny().orElseThrow(() ->
            new NostrException(
                String.format("BadgeDefinitionAwardEvent\n%s\nmissing required RelayTag", event.createPrettyPrintJson())));

    if (!superconductorRelayUrl.equals(eventRelaysTagUrl))
      throw new NostrException(
          String.format("BadgeDefinitionAwardEvent RelayTag URL: [%s] does not match host SuperConductor relay URL: [%s]",
              eventRelaysTagUrl, superconductorRelayUrl));

    super.processIncomingEvent(event);
  }

  @Override
  public BadgeDefinitionGenericEvent materialize(EventIF eventIF) {
    BadgeDefinitionGenericEvent materialize = cacheBadgeDefinitionGenericEventServiceIF.materialize(eventIF);
    return materialize;
  }
}
