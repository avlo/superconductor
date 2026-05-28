package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionGenericEventKindPlugin extends NonPublishingEventKindPlugin {
  public BadgeDefinitionGenericEventKindPlugin(@NonNull EventPluginIF eventPluginIF) {
    super(eventPluginIF);
  }

//  TODO: create IF of this class, overriding:
//      default <T extends BaseEvent> void processIncomingEvent(@NonNull T event)
//    with concrete method calling default method and returning
//      BadgeDefinitionGenericEvent void processIncomingEvent(@NonNull T event)

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    log.debug("processing incoming BadgeDefinitionGenericEvent:\n  {}", event.createPrettyPrintJson());
    event.findFirstTag(RelayTag.class)
        .orElseThrow(() ->
            new NostrException(
                String.format("BadgeDefinitionAwardEvent\n%s\nmissing required RelayTag", event.createPrettyPrintJson())));
    return super.processIncomingEvent(event);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
