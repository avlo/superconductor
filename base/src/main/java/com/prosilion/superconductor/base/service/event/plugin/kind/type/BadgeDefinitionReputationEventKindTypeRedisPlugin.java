package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.RelayTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionReputationEventKindTypeRedisPlugin extends NonPublishingEventKindTypePlugin {
  private final String superconductorRelayUrl;

  public BadgeDefinitionReputationEventKindTypeRedisPlugin(
      @NonNull String superconductorRelayUrl,
      @NonNull EventKindTypePluginIF eventKindTypePlugin) {
    super(eventKindTypePlugin);
    this.superconductorRelayUrl = superconductorRelayUrl;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
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

    super.processIncomingEvent(event);
  }
}
