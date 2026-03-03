package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class FollowSetsEventKindPlugin extends PublishingEventKindPlugin {

  public FollowSetsEventKindPlugin(
      @NonNull NotifierService notifierService,
      @NonNull EventPlugin eventPlugin) {
    super(notifierService, eventPlugin);
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
