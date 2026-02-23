package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.BadgeAwardGenericEvent;
import com.prosilion.nostr.event.BadgeDefinitionGenericEvent;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardGenericEventKindRedisPlugin<S extends BadgeDefinitionGenericEvent, T extends BadgeAwardGenericEvent<S>> extends PublishingEventKindPlugin {

  public BadgeAwardGenericEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPlugin eventPlugin) {
    super(notifierService, eventPlugin);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
