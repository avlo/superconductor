package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// our SportsCar extends CarDecorator
public class StandardEventKindPlugin extends PublishingEventKindPlugin {
  private final Kind kind;

  public StandardEventKindPlugin(
     @NonNull Kind standardKind,
     @NonNull NotifierService notifierService,
     @NonNull EventPlugin eventPlugin) {
    super(notifierService, eventPlugin);
    this.kind = standardKind;
  }

  @Override
  public Kind getKind() {
    return kind;
  }
}
