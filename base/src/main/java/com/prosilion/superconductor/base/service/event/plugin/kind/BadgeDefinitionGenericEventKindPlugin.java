package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionGenericEventKindPlugin extends NonPublishingEventKindPlugin {
  public BadgeDefinitionGenericEventKindPlugin(@NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
  }

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    Optional<RelayTag> eventRelayTag = event.findFirstTag(RelayTag.class);
    log.debug("processing incoming BadgeDefinitionGenericEvent using eventRelayTag url [{}]",
       eventRelayTag.map(RelayTag::getRelay).map(Relay::getUrl).orElse("NULL"));
    return super.processIncomingEvent(event, fromRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
