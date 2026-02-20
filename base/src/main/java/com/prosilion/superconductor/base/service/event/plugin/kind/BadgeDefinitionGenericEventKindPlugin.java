package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.NostrException;
import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.GenericEventRecord;
import com.prosilion.nostr.event.internal.Relay;
import com.prosilion.nostr.filter.Filterable;
import com.prosilion.nostr.tag.RelayTag;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeDefinitionGenericEventKindPlugin extends NonPublishingEventKindPlugin {
  @NonNull Function<EventIF, BadgeDefinitionGenericEvent> eventMaterializer;

  public BadgeDefinitionGenericEventKindPlugin(
      @NonNull EventPlugin eventPlugin,
      @NonNull Function<EventIF, BadgeDefinitionGenericEvent> eventMaterializer) {
    super(eventPlugin);
    this.eventMaterializer = eventMaterializer;
  }

//  TODO: create IF of this class, overriding:
//      default <T extends BaseEvent> void processIncomingEvent(@NonNull T event)
//    with concrete method calling default method and returning
//      BadgeDefinitionGenericEvent void processIncomingEvent(@NonNull T event)

  @Override
  public GenericEventRecord processIncomingEvent(@NonNull EventIF event) {
    log.debug("processing incoming BadgeDefinitionGenericEvent:\n  {}", event.createPrettyPrintJson());
    List<String> relaysTagUrls = Filterable.getTypeSpecificTagsStream(RelayTag.class, event)
        .map(RelayTag::getRelay)
        .map(Relay::getUrl)
        .toList();

    String eventRelaysTagUrl = relaysTagUrls.stream()
        .findAny().orElseThrow(() ->
            new NostrException(
                String.format("BadgeDefinitionAwardEvent\n%s\nmissing required RelayTag", event.createPrettyPrintJson())));

//  TODO: either:
//    1) cycle through relaysTagUrls and validate (at least one) exists, or
//    2) (currently, below) grab first one and continue (or validate exists)

    return super.processIncomingEvent(
        eventMaterializer.apply(event));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_DEFINITION_EVENT;
  }
}
