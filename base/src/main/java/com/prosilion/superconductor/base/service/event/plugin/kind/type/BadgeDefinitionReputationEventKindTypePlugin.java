package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.RelayTag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionReputationEventKindTypePlugin extends NonPublishingEventKindTypePlugin {
  private final String superconductorRelayUrl;

  public BadgeDefinitionReputationEventKindTypePlugin(
      @NonNull String superconductorRelayUrl,
      @NonNull EventKindTypePluginIF eventKindTypePlugin) {
    super(eventKindTypePlugin);
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

    return super.processIncomingEvent(event);
  }
}
