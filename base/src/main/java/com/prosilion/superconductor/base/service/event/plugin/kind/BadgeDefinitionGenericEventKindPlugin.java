package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.nostr.user.Identity;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionGenericEventKindPlugin extends NonPublishingEventKindPlugin {
  private final Identity superconductorInstanceIdentity;

  public BadgeDefinitionGenericEventKindPlugin(
     @NonNull Identity superconductorInstanceIdentity,
     @NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
    this.superconductorInstanceIdentity = superconductorInstanceIdentity;
  }

//  TODO: create IF of this class, overriding:
//      default <T extends BaseEvent> void processIncomingEvent(@NonNull T event)
//    with concrete method calling default method and returning
//      BadgeDefinitionGenericEvent void processIncomingEvent(@NonNull T event)

  @Override
  public Optional<GenericEventRecord> processIncomingEvent(@NonNull EventIF event, @NonNull Relay fromRelay) {
    Optional<RelayTag> eventRelayTag = event.findFirstTag(RelayTag.class);
    Relay finalRelay = eventRelayTag.map(RelayTag::relay).orElse(fromRelay);
    log.debug("processing incoming BadgeDefinitionGenericEvent using [{}] url [{}]",
       eventRelayTag.isPresent() ? "event RelayTag" : "fromRelay", finalRelay.getUrl());
    return super.processIncomingEvent(
       eventRelayTag.isPresent() ?
          event :
          new BadgeDefinitionGenericEvent(
             superconductorInstanceIdentity,
             event.asGenericEventRecord(),
             fromRelay),
       finalRelay);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
