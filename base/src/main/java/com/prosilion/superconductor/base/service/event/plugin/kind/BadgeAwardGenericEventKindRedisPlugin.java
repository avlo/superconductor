package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardGenericEventKindRedisPlugin<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends PublishingEventKindPlugin {
  @NonNull Function<EventIF, T> eventMaterializer;

  public BadgeAwardGenericEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull Function<EventIF, T> eventMaterializer) {
    super(notifierService, eventPlugin);
    this.eventMaterializer = eventMaterializer;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF event) {
    super.processIncomingEvent(
        eventMaterializer.apply(event));
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
