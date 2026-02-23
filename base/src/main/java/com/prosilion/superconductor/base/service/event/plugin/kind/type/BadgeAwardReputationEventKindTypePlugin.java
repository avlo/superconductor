package com.prosilion.superconductor.base.service.event.plugin.kind.type;

import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class BadgeAwardReputationEventKindTypePlugin extends PublishingEventKindTypePlugin {
  public BadgeAwardReputationEventKindTypePlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventKindTypePluginIF eventKindTypePlugin) {
    super(notifierService, eventKindTypePlugin);
  }
}
