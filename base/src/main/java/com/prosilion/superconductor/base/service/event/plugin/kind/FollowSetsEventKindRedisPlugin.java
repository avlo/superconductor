package com.prosilion.superconductor.base.service.event.plugin.kind;

import com.prosilion.nostr.enums.Kind;
import com.prosilion.nostr.event.EventIF;
import com.prosilion.nostr.event.FollowSetsEvent;
import com.prosilion.superconductor.base.service.event.plugin.EventPlugin;
import com.prosilion.superconductor.base.service.request.subscriber.NotifierService;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.NonNull;

@Slf4j
// our SportsCar extends CarDecorator
public class FollowSetsEventKindRedisPlugin extends PublishingEventKindPlugin {
  @NonNull Function<EventIF, FollowSetsEvent> eventMaterializer;

  public FollowSetsEventKindRedisPlugin(
      @NonNull NotifierService notifierService,
      @NonNull @Qualifier("eventPlugin") EventPlugin eventPlugin,
      @NonNull Function<EventIF, FollowSetsEvent> eventMaterializer) {
    super(notifierService, eventPlugin);
    this.eventMaterializer = eventMaterializer;
  }

  @Override
  public void processIncomingEvent(@NonNull EventIF incomingFollowSetsEvent) {
    super.processIncomingEvent(
        eventMaterializer.apply(incomingFollowSetsEvent));
  }

  @Override
  public Kind getKind() {
    return Kind.FOLLOW_SETS;
  }
}
