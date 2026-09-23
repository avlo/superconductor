package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.plugin.EventPluginIF;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardCanonicalEventKindPlugin extends PublishingEventKindPlugin {

  public BadgeAwardCanonicalEventKindPlugin(
     @NonNull NotifierService notifierService,
     @NonNull EventPluginIF eventPluginIF) {
    super(notifierService, eventPluginIF);
  }

  @Override
  public Kind getKind() {
    return Kind.BADGE_AWARD_EVENT;
  }
}
